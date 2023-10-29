const host = 'http://' + window.location.host
let responseData = null;
const url = window.location.href;
const segments = url.split("/");
const type = segments[segments.length - 5]; // 뒤에서 다섯 번째 segment
const routeId = segments[segments.length - 4]; // 뒤에서 네 번째 segment
const originAddress = segments[segments.length - 3]; // 뒤에서 세 번째 segment
const destinationAddress = segments[segments.length - 2]; // 뒤에서 두 번째 segment
const redius = segments[segments.length - 1]; // 마지막 segment
$(document).ready(function() {
    if (type === 'save') {
        makeHistoryMap(routeId);
    } else if (type === 'live') {
        makeNavi(originAddress, destinationAddress);
    } else if (type === 'live-random') {
        makeRandomNavi(originAddress, destinationAddress, redius);
    } else if (type === 'live-all-random') {
        makeAllRandomNavi(originAddress, redius);
    } else {
        return;
    }
});

// 예 버튼
document.getElementsByClassName('button-yes')[0].addEventListener('click', function() {
    if (type === 'live') {
        saveRoute(responseData, originAddress, destinationAddress)
    } else if (type === 'live-random') {
        saveRoute(responseData, originAddress, destinationAddress)
    } else if (type === 'live-all-random') {
        saveRoute(responseData, originAddress, '무작위 주소')
    } else {
        return;
    }
});

// 아니오 버튼
document.getElementsByClassName('button-no')[0].addEventListener('click', function() {
    window.location.href = '/api/home';
});

// kakaomap 표시 해주는 곳-----------------------------------------------------------------------------------------------------//
var container = document.getElementById('map');
var options = {
    center: new kakao.maps.LatLng(33.450701, 126.570667),
    level: 3
};
map = new kakao.maps.Map(container, options);

// 경로 안내 polyline ----------------------------------------------------------------------------------------------------------//
var polylines = [];
function clearPolylines() {
    for (let i = 0; i < polylines.length; i++) {
        polylines[i].setMap(null);
    }
    polylines = [];
}

// histories.js에서 넘어온 데이터로 지도정보 구성
function makeHistoryMap(routeId) {
    fetch('/api/route/' + routeId)
        .then(response => response.json())
        .then(data => {
            responseData = data;
            routeData = data;
            if (!map) {
                map = new kakao.maps.Map(document.getElementById('map'), {
                    level: 3
                });
            }
            let bound = data.bounds;
            let distance = data.distance;
            let duration = data.duration;

            // distance와 duration을 표시할 요소를 선택
            let distanceElement = document.querySelector('.distance');
            let durationElement = document.querySelector('.duration');

            let hour = Math.floor(duration / 3600);
            let minute = Math.floor((duration % 3600) / 60);

            let km= (distance / 1000).toFixed(1);


            // 요소에 데이터를 추가
            distanceElement.textContent = '소요 시간: ' + hour + '시간 ' + minute + '분';
            durationElement.textContent = '총 거리: ' + km + ' km';


            var bounds = new kakao.maps.LatLngBounds(
                new kakao.maps.LatLng(bound.min_y, bound.min_x),
                new kakao.maps.LatLng(bound.max_y, bound.max_x)
            );

            map.setBounds(bounds);


            for(let road of data.roads){
                let path = [];
                for(let i=0; i<road.vertexes.length; i+=2){
                    path.push(new kakao.maps.LatLng(road.vertexes[i+1], road.vertexes[i]));
                }

                const polyline = new kakao.maps.Polyline({
                    path: path,
                    strokeWeight: 5,
                    strokeColor: '#007bff',
                    strokeOpacity: 0.7,
                    strokeStyle: 'solid'
                });

                polyline.setMap(map);

                polylines.push(polyline);
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

// 기본 길찾기 동작
function makeNavi(originAddress, destinationAddress) {
    setToken();
    fetch('/route?originAddress=' + originAddress  + '&destinationAddress=' + destinationAddress)
        .then(response => response.json())
        .then(data => {
            responseData = data;
            adapt_KakaoResponseToRouteData(data);
            makeLiveMap(data)
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

// 목적지 기반 랜덤 길찾기 동작
function makeRandomNavi(originAddress, destinationAddress, redius) {
    setToken();
    fetch('/random-route?originAddress=' + originAddress  + '&destinationAddress=' + destinationAddress + '&redius=' + redius)
        .then(response => response.json())
        .then(data => {
            responseData = data;
            adapt_KakaoResponseToRouteData(data);
            makeLiveMap(data)
        })
        .catch(error => {
            console.error('Random-Error:', error);
        });
}

// 반경 기반 랜덤 길찾기 동작
function makeAllRandomNavi(originAddress, redius) {
    const auth = getToken();
    fetch(`/all-random-route?originAddress=${originAddress}&redius=${redius}`, {
        method: 'GET',
        headers: {
            'Authorization': auth // 토큰을 Authorization 헤더에 실어 보냄
        }
    })
        .then(response => response.json())
        .then(data => {
            responseData = data;
            adapt_KakaoResponseToRouteData(data);
            makeLiveMap(data)
        })
        .catch(error => {
            console.error('All-Random-Error:', error);
        });
}


// 신규 길찾기, 랜덤 길찾기에서 얻은 response를 가공해서 Map에 띄워줌
function makeLiveMap(data) {
    clearPolylines(); // 기존의 선들을 모두 제거
    if (!map) {
        map = new kakao.maps.Map(document.getElementById('map'), {
            level: 3
        });
    }

    var bounds = new kakao.maps.LatLngBounds(); // 모든 경로의 좌표를 포함할 수 있는 경계 객체를 만듭니다.

    // 경로 정보(routes)의 각 섹션(section)별로 반복하여 처리합니다.
    for (let route of data.routes) {
        let distance = route.summary.distance;
        let duration = route.summary.duration;

        // distance와 duration을 표시할 요소를 선택
        let distanceElement = document.querySelector('.distance');
        let durationElement = document.querySelector('.duration');

        let hour = Math.floor(duration / 3600);
        let minute = Math.floor((duration % 3600) / 60);
        let km = (distance / 1000).toFixed(1);

        // 요소에 데이터를 추가
        distanceElement.textContent = '소요 시간: ' + hour + '시간 ' + minute + '분';
        durationElement.textContent = '총 거리: ' + km + ' km';

        for (let section of route.sections) {
            // 각 섹션의 경로를 순회하면서 모든 좌표를 경계 객체에 추가합니다.
            for (let road of section.roads) {
                let path = [];
                for (let i = 0; i < road.vertexes.length; i += 2) {
                    let latLng = new kakao.maps.LatLng(road.vertexes[i + 1], road.vertexes[i]);
                    bounds.extend(latLng); // 경계를 확장하여 이 좌표를 포함하도록 합니다.
                    path.push(latLng);
                }

                let polyline = new kakao.maps.Polyline({
                    path: path,
                    strokeWeight: 5,
                    strokeColor: '#007bff',
                    strokeOpacity: 0.7,
                    strokeStyle: 'solid'
                });

                polyline.setMap(map);

                polylines.push(polyline); // 선을 배열에 추가
            }
        }
    }

    map.setBounds(bounds); // 새로 계산된 경계를 지도에 적용합니다.
}


// 경로 기록
function saveRoute(data, originAddress, destinationAddress) {

    const auth = getToken();
    var decodedOriginAddress = decodeURIComponent(originAddress);
    var decodedDestinationAddress = decodeURIComponent(destinationAddress);

    fetch('/api/routes', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': auth // 인증 토큰을 Authorization 헤더에 추가
        },
        body: JSON.stringify({
            requestData: data, // KakaoRouteAllResponseDto 객체
            originAddress: decodedOriginAddress,
            destinationAddress: decodedDestinationAddress,
            mapType: type
        })
    })
}

// 토큰 세팅하기
function setToken() {
    const auth = getToken();
    if (auth !== undefined && auth !== '') {
        $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
            jqXHR.setRequestHeader('Authorization', auth);
        });
    } else {
        window.location.href = host + '/api/user/login-page';
        return;
    }
}

// 토큰 가져오기
function getToken() {

    let auth = Cookies.get('Authorization');

    if(auth === undefined) {
        return '';
    }

    // kakao 로그인 사용한 경우 Bearer 추가
    if(auth.indexOf('Bearer') === -1 && auth !== ''){
        auth = 'Bearer ' + auth;
    }
    return auth;
}

// 상세교통상황 표시
// map.addOverlayMapTypeId(kakao.maps.MapTypeId.TRAFFIC);

function onClick_StartNavi_navi()
{
    if (type === 'save') {
        // getSaveNextGuidPoint(false);
        // getSaveGuidPoint(true);
        // startCorutine();
    }
    else {
        // getNextGuidPoint(false);
        // getGuidPoint(true);
        // startCorutine();

    }
    updateMark();

    //guid_info
    if(!document.getElementById("input_StartNavi").classList.contains("disabled"))
        document.getElementById("input_StartNavi").classList.add("disabled");
    if(document.getElementById("guid_info").classList.contains("disabled"))
        document.getElementById("guid_info").classList.remove("disabled");


    if (routeData != null) {
        if (type === 'save') {
            // update(pathData.roads(0).vertexes[1], pathData.roads(0).vertexes[0]);
            // map.setLevel(3, {animate: true});// 사용시 보이는 위치 달라짐
            // panTo(pathData.roads(0).vertexes[1], pathData.roads(0).vertexes[0]);
        }

        else {

        }

        let startPoint = routeData.guides[0];
        update(startPoint.y, startPoint.x);

        map.setLevel(3, {animate: true});// 사용시 보이는 위치 달라짐
        panTo(startPoint.y, startPoint.x);
    }

}
function onClick_StopNavi_navi()
{
    if(document.getElementById("input_StartNavi").classList.contains("disabled"))
        document.getElementById("input_StartNavi").classList.remove("disabled");
    if(!document.getElementById("guid_info").classList.contains("disabled"))
        document.getElementById("guid_info").classList.add("disabled");

    stopNavi();
    window.location.href = host + '/api/home';
}

function Update_GuidIndo_navi()
{
    // let nextGuid = getGuidPoint(false);
    let nextGuid = routeData.guides[naviInfo_ProcessIndex];

    // document.getElementById('guid-Distance').innerText = "전방 " + nextGuidDistacne.toFixed(1) + "m 에서 " + data.guidance;
    // document.getElementById('guid-EnterTime').innerText = "다음 안내 까지 : " + nexGuidDuration.toFixed(1) + "s";
    // document.getElementById('guid-Des-Distance').innerText = pathLeftDistance.toFixed(1) + "m";
    // document.getElementById('guid-Des-Time').innerText = pathLeftDuration.toFixed(1) + "s";

    console.log("전방 " + visibilityDistance(nextGuidDistacne) + " 에서 " + nextGuid.guidance);
    console.log("다음 안내까지 : " + visibilityTime(nextGuidDuration));
    console.log("도착까지  " + visibilityDistance(pathLeftDistance) + " / " + visibilityTime(pathLeftDuration));

    document.getElementById('guid_ance').innerText
        = "전방 " + visibilityDistance(nextGuidDistacne) + " 에서\n" + nextGuid.guidance;
    document.getElementById('guid_left').innerText
        =   "도착까지  " + visibilityDistance(pathLeftDistance) + " / " + visibilityTime(pathLeftDuration);

}

function visibilityDistance(dis = 0)
{
    if(dis == null)
        return "0m";

    if (Math.floor(dis * 0.001) > 0)
    {
        return (dis * 0.001).toFixed(1) + "km";
    }else
    {
        return dis.toFixed(1) + "m";
    }
}
function visibilityTime(dur = 0)
{
    if (dur == null)
        return "0분";

    const hours = Math.floor(dur / 3600); // 초를 시간으로 변환
    const minutes = Math.floor((dur % 3600) / 60); // 초를 분으로 변환하고 소수점 첫 번째 자리까지 나타내기

    if (hours > 0)
    {
        return `${hours}시간${minutes}분`;
    }else
    {
        return `${minutes}분`;
    }
}