var pathData;//마지막 길 찾은 정보 - 네비게이션 디버깅에 쓰임

//길 정보 , staticVariable.adapt_KakaoResponseToRouteData() 에서 설정됨
var routeData = {
    originAddress: "",
    destinationAddress: "",
    distance: 0,
    duration: 0,
    bounds:
        {
            min_x: 0,
            min_y: 0,
            max_x: 0,
            max_y: 0
        },
    guides: [],//카카오 응답 객체 그대로
    roads: [[{value: 0}]],//이중 배열로 카카오 응답 객체의 Route > sections > roads 의 vertextes만 있는 버전
    createAt: ""
}
//staticVariable.adapt_KakaoResponseToRouteData() 에서 설정됨
var startLocation = {lat : 0, lng : 0};
//staticVariable.adapt_KakaoResponseToRouteData() 에서 설정됨
var destinationLocation = {lat : 0, lng : 0};

var offetUserRadius = 20;


var positionMark = new kakao.maps.Marker();
var positionText = new kakao.maps.InfoWindow();
var naviInfoMark = new kakao.maps.Marker();
var naviInfoText = new kakao.maps.InfoWindow();


var naviInfo_SectionIndex = 0;
var naviInfo_GuidIndex = 1;

var naviInfo_ProcessIndex = 1;

//0 : 이미 도착지점일때 , -1 : 유효하지 않은 경로일때 , 1 : 성공 , 2 : 구간 시작점, 3 : 다음이 도착지점일때
var naviInfo_State = -1;

var pathLeftDistance = 0;
var pathLeftDuration = 0;
var nextGuidDistacne = 0;
var nextGuidDuration = 0;
var map;

let intervalId;

function startCorutine()
{
    clearInterval(intervalId);

    intervalId = setInterval(function () {
        navigator.geolocation.getCurrentPosition(function(position) {
            // 위치 정보를 표시하기
            // $("#location2").text("\n, GPS 위치 정보: " + position.coords.latitude + ", " + position.coords.longitude);
            EditMark(positionMark, positionText, position.coords.latitude, position.coords.longitude, '현위치');
            update(position.coords.latitude, position.coords.longitude);
            console.log("위치 업데이트");
        });
    }, 1000);
}
function stopCoroutine()
{
    clearInterval(intervalId);

    intervalId = -1;

    console.warn("Stop Coroutine");
}


async function adapt_histories(route_id = -1)
{
    if (route_id <= 0) return;

    try {
        const response = await fetch('/api/route/' + route_id);
        routeData = await response.json();
        console.log("loaded : " + routeData);

    }catch (e)
    {
        console.warn(e.message);
    }
}

//Set routeData , stratLocation, destinationLocation
function adapt_KakaoResponseToRouteData(kakaoRes) {

    if (kakaoRes == null)
        console.warn("parameter is null");

    //try
    {
        routeData = {
            originAddress: "",
            destinationAddress: "",
            distance: 0,
            duration: 0,
            bounds:
                {
                    min_x: 0,
                    min_y: 0,
                    max_x: 0,
                    max_y: 0
                },
            guides: [],
            roads: [[{value: 0}]],
            createAt: ""
        }


        routeData.originAddress = kakaoRes.routes[0].summary.origin.name;//----이름이 없음! 대신 위경도 값은 있음
        routeData.destinationAddress = kakaoRes.routes[0].summary.destination.name;//----이름이 없음! 대신 위경도 값은 있음
        //(kakaoRes.routes[0].summary.destination.y , kakaoRes.routes[0].summary.destination.x) - 따로 저장?

        {
            stratLocation = {lat: kakaoRes.routes[0].summary.origin.y, lng: kakaoRes.routes[0].summary.origin.x};
            destinationLocation = {lat: kakaoRes.routes[0].summary.destination.y, lng: kakaoRes.routes[0].summary.destination.x};
        }

        routeData.distance = kakaoRes.routes[0].summary.distance;
        routeData.duration = kakaoRes.routes[0].summary.duration;
        routeData.bounds = {
            min_x: kakaoRes.routes[0].summary.bound.min_x,
            min_y: kakaoRes.routes[0].summary.bound.min_y,
            max_x: kakaoRes.routes[0].summary.bound.max_x,
            max_y: kakaoRes.routes[0].summary.bound.max_y
        };
        // routeData.createAt

        //출발지 - 경유지 / 경유지 ~ 목적지
        // == guides 와 roads 부분  이차 배열 형태를 일차 배열으로 바꿔서 설정

        routeData.roads = [];

        for (let sec = 0; sec < pathData.routes[0].sections.length; sec++) {
            routeData.roads.push([]);
            for (let road = 0; road < pathData.routes[0].sections[sec].roads.length; road++) {
                routeData.roads[sec].push(pathData.routes[0].sections[sec].roads[road].vertexes);
            }
        }

        // for (let sec = 0; sec < pathData.routes[0].sections.length; sec++) {
        //     for (let road = 0; road < pathData.routes[0].sections[sec].roads.length; road++) {
        //         for(let i = 0; i < pathData.routes[0].sections[sec].roads[road].vertexes.length; i += 2)
        //         {
        //             let lroads = pathData.routes[0].sections[sec].roads[road];
        //
        //             if (i > 1)
        //             {
        //                 if ((lroads.vertexes[i] === lroads.vertexes[i - 2]) && (lroads.vertexes[i + 1] === lroads.vertexes[i - 1]))
        //                 {
        //                     console.log("Equal road");
        //                     continue;
        //                 }
        //             }
        //
        //             routeData.roads.push(pathData.routes[0].sections[sec].roads[road].vertexes[i]);
        //             routeData.roads.push(pathData.routes[0].sections[sec].roads[road].vertexes[i + 1]);
        //         }
        //     }
        // }


        routeData.guides = [];
        routeData.guides.push(pathData.routes[0].sections[0].guides[0]);
        for (let sec = 0; sec < pathData.routes[0].sections.length; sec++) {
            for (let guid = 1; guid < pathData.routes[0].sections[sec].guides.length; guid++) {
                routeData.guides.push(pathData.routes[0].sections[sec].guides[guid]);
            }
        }

    }
    // catch (e)
    // {
    //     console.warn(e.message);
    //     routeData = null;
    // }



    // == 경유지 지점 정보가 사라짐 -> 가이드 에서 type이 1000 이면 경유지

    //=== 문제 : 도로에 겹치는 부분이 가끔 있음 - 특정 조건에 의해 발생하는거 같음
}