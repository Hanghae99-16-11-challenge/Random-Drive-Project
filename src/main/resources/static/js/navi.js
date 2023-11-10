const host = 'http://' + window.location.host
let responseData = null;
const url = window.location.href;
const segments = decodeURIComponent(url).split("/");
// save , live , live-random , live-all-random
const type = segments[segments.length - 7];
const routeId = segments[segments.length - 6];
const originAddress = segments[segments.length - 5];
const destinationAddress = segments[segments.length - 4];
const redius = segments[segments.length - 3];
const waypointNum = segments[segments.length - 2];
const secondType = segments[segments.length - 1];
$(document).ready(function () {
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
// document.getElementsByClassName('button-yes')[0].addEventListener('click', function () {
//     console.log(type);
//     if (type === 'live') {
//         saveRoute(responseData, originAddress, destinationAddress)
//     } else if (type === 'live-random') {
//         saveRoute(responseData, originAddress, destinationAddress)
//     } else if (type === 'live-all-random') {
//         saveRoute(responseData, originAddress, '무작위 주소')
//     } else {
//         return;
//     }
// });

// 아니오 버튼
document.getElementsByClassName('button-no')[0].addEventListener('click', function () {
    window.location.href = '/view/home';
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
                waypointCount = 0;
                responseData = data;
                routeData = data

                if (!map) {
                    map = new kakao.maps.Map(document.getElementById('map'), {
                        level: 3
                    });
                }
                // 히스토리 마커용 추가

                // // Initialize markers for departure and arrival
                // let departureMarker, arrivalMarker;
                //
                // for (var i = 0; i < data.guides.length; i++) {
                //     if (data.guides[i].type === 100) {
                //         var lat_ori = data.guides[i].y;
                //         var lng_ori = data.guides[i].x;
                //
                //         departureMarker = new kakao.maps.Marker({
                //             position: new kakao.maps.LatLng(lat_ori, lng_ori),
                //             title: '출발',
                //             image: 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/red_b.png'
                //         });
                //     }
                //
                //     if (data.guides[i].type === 101) {
                //         var lat_des = data.guides[i].y;
                //         var lng_des = data.guides[i].x;
                //
                //         arrivalMarker = new kakao.maps.Marker({
                //             position: new kakao.maps.LatLng(lat_des, lng_des),
                //             title: '도착',
                //             image: 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/blue_b.png'
                //         });
                //     }
                // }

                var imageSize = new kakao.maps.Size(30, 40);
                for (var i = 0; i < data.guides.length; i++) {
                    if (data.guides[i].type === 100) {
                        var lat_ori = data.guides[i].y;
                        var lng_ori = data.guides[i].x;
                    }

                    if (data.guides[i].type === 101) {
                        var lat_des = data.guides[i].y;
                        var lng_des = data.guides[i].x;
                    }


                    var positions = [
                        {
                            title: '출발',
                            latlng: new kakao.maps.LatLng(lat_ori, lng_ori),
                            image: 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/red_b.png'
                            //"https://cdn-icons-png.flaticon.com/512/6213/6213694.png"
                        },
                        {
                            title: '도착',
                            latlng: new kakao.maps.LatLng(lat_des, lng_des),
                            image: 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/blue_b.png'
                            //"https://cdn-icons-png.flaticon.com/512/4856/4856582.png"
                        }
                    ]

                }
                // 마커를 지도에 표시하기
                for (var i = 0; i < positions.length; i++) {
                    // 마커 이미지 크기

                    var markerImage = new kakao.maps.MarkerImage(positions[i].image, imageSize);
                    var marker = new kakao.maps.Marker({
                        map: map,
                        position: positions[i].latlng,
                        title: positions[i].title,
                        image: markerImage,
                    })
                }

                // 경유지 마커 표시
                var imageWay = 'https://cdn-icons-png.flaticon.com/128/4198/4198066.png';
                var imageSize = new kakao.maps.Size(40, 35);
                // 마커 이미지를 생성합니다
                var markerImage = new kakao.maps.MarkerImage(imageWay, imageSize);
                // 경유지 마커 표시하기
                for (var i = 0; i < data.guides.length; i++) {
                    if (data.guides[i].type === 1000) {
                        var lat_way = data.guides[i].y;
                        var lon_way = data.guides[i].x;

                        var latlng = new kakao.maps.LatLng(lat_way, lon_way)

                        var marker = new kakao.maps.Marker({
                            map: map,
                            position: latlng,
                            image: markerImage
                        });
                    }
                }

                marker.setMap(map)


                // 기존 코드
                let bound = data.bounds;
                let distance = data.distance;
                let duration = data.duration;

                // distance와 duration을 표시할 요소를 선택
                let distanceElement = document.querySelector('.distance');
                let durationElement = document.querySelector('.duration');

                let hour = Math.floor(duration / 3600);
                let minute = Math.floor((duration % 3600) / 60);

                let km = (distance / 1000).toFixed(1);


                // 요소에 데이터를 추가
                distanceElement.textContent = '소요 시간: ' + hour + '시간 ' + minute + '분';
                durationElement.textContent = '총 거리: ' + km + ' km';


                var bounds = new kakao.maps.LatLngBounds(
                    new kakao.maps.LatLng(bound.min_y, bound.min_x),
                    new kakao.maps.LatLng(bound.max_y, bound.max_x)
                );

                map.setBounds(bounds);


                for (let road of data.roads) {
                    let path = [];
                    for (let i = 0; i < road.vertexes.length; i += 2) {
                        path.push(new kakao.maps.LatLng(road.vertexes[i + 1], road.vertexes[i]));
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
            }
        )
        .catch(error => failedFindRoute);

}

// 기본 길찾기 동작
function makeNavi(originAddress, destinationAddress) {
    setToken();
    fetch('/api/route?originAddress=' + originAddress + '&destinationAddress=' + destinationAddress)
        .then(response => response.json())
        .then(data => {
            responseData = data;
            adapt_KakaoResponseToRouteData(data);
            makeLiveMap(data)
            makeMarker(data)
        })
        .catch(error => failedFindRoute);
}

// 목적지 기반 랜덤 길찾기 동작
function makeRandomNavi(originAddress, destinationAddress, redius) {
    setToken();
    if (secondType === 'line') {
        fetch('/api/line-random-route?originAddress=' + originAddress + '&destinationAddress=' + destinationAddress + '&count=' + waypointNum)
            .then(response => response.json())
            .then(data => {
                responseData = data;
                adapt_KakaoResponseToRouteData(data);
                makeLiveMap(data);
                makeMarker(data);
            })
            .catch(error => failedFindRoute);
    } else if (secondType === 'zigzag') {
        fetch('/api/zigzag-random-route?originAddress=' + originAddress + '&destinationAddress=' + destinationAddress + '&count=' + waypointNum)
            .then(response => response.json())
            .then(data => {
                responseData = data;
                adapt_KakaoResponseToRouteData(data);
                makeLiveMap(data)
                makeMarker(data);
            })
            .catch(error => failedFindRoute);
    } else {
        fetch('/api/random-route?originAddress=' + originAddress + '&destinationAddress=' + destinationAddress + '&redius=' + redius)
            .then(response => response.json())
            .then(data => {
                responseData = data;
                adapt_KakaoResponseToRouteData(data);
                makeLiveMap(data)
                makeMarker(data);
            })
            .catch(error => failedFindRoute);
    }
}

// 반경 기반 랜덤 길찾기 동작
function makeAllRandomNavi(originAddress, redius) {
    const auth = getToken();

    if (secondType === 'line') {
        fetch(`/api/line-all-random-route?originAddress=${originAddress}&distance=${redius}&count=${waypointNum}`, {
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
                makeMarker(data);
            })
            .catch(error => failedFindRoute);
    } else if (secondType === 'zigzag') {
        fetch(`/api/zigzag-all-random-route?originAddress=${originAddress}&distance=${redius}&count=${waypointNum}`, {
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
                makeMarker(data);
            })
            .catch(error => failedFindRoute);
    } else if (secondType === 'circle') {
        fetch(`/api/circle-all-random-route?originAddress=${originAddress}&distance=${redius}&count=${waypointNum}`, {
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
                makeMarker(data);
            })
            .catch(error => failedFindRoute);
    } else {
        fetch(`/api/all-random-route?originAddress=${originAddress}&redius=${redius}`, {
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
                makeMarker(data);
            })
            .catch(error => failedFindRoute);
    }
}
function failedFindRoute(error)
{
    {
        const auth = getToken();

        if (auth === undefined || auth === '') {

            alert("다시 로그인 해주세요...");
            window.location.href = host + '/view/user/login-page';
            return;
        }

        //            $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
        //                 jqXHR.setRequestHeader('Authorization', auth);
        //             });
    }//우선 토큰 검사

    alert("경로를 생성할 수 없습니다. 다시 시도해 주세요");
    console.log(error);
    window.location.href = '/view/home';
}

// 신규 길찾기, 랜덤 길찾기에서 얻은 response를 가공해서 Map에 띄워줌
function makeLiveMap(data) {
    waypointCount = 0;
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

    console.log(decodedOriginAddress);

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
        window.location.href = host + '/view/user/login-page';
        return;
    }
}

// 토큰 가져오기
function getToken() {

    let auth = Cookies.get('Authorization');

    if (auth === undefined) {
        return '';
    }

    // kakao 로그인 사용한 경우 Bearer 추가
    if (auth.indexOf('Bearer') === -1 && auth !== '') {
        auth = 'Bearer ' + auth;
    }
    return auth;
}

// 상세교통상황 표시
// map.addOverlayMapTypeId(kakao.maps.MapTypeId.TRAFFIC);


function onClick_StartNavi_navi(updateLocation = true) {

    saveNavi();
    updateMark();

    //guid_info
    if (!document.getElementById("input_StartNavi").classList.contains("disabled"))
        document.getElementById("input_StartNavi").classList.add("disabled");
    if (document.getElementById("guid_info").classList.contains("disabled"))
        document.getElementById("guid_info").classList.remove("disabled");


    if (routeData != null) {
        if (type === 'save') {
            // update(pathData.roads(0).vertexes[1], pathData.roads(0).vertexes[0]);
            // map.setLevel(3, {animate: true});// 사용시 보이는 위치 달라짐
            // panTo(pathData.roads(0).vertexes[1], pathData.roads(0).vertexes[0]);
        } else {

        }

        let startPoint = routeData.guides[0];
        update(startPoint.y, startPoint.x);

        map.setLevel(3, {animate: true});// 사용시 보이는 위치 달라짐
        panTo(startPoint.y, startPoint.x);

        speakText("안내를 시작합니다.");
        aunceGuid(false);

        if (updateLocation)
            startCorutine();
    }

}

function onClick_StopNavi_navi() {
    if (document.getElementById("input_StartNavi").classList.contains("disabled"))
        document.getElementById("input_StartNavi").classList.remove("disabled");
    if (!document.getElementById("guid_info").classList.contains("disabled"))
        document.getElementById("guid_info").classList.add("disabled");

    stopNavi();
    window.location.href = host + '/view/home';
}

function Update_GuidIndo_navi() {
    // let nextGuid = getGuidPoint(false);
    let nextGuid = routeData.guides[naviInfo_ProcessIndex];

    // document.getElementById('guid-Distance').innerText = "전방 " + nextGuidDistacne.toFixed(1) + "m 에서 " + data.guidance;
    // document.getElementById('guid-EnterTime').innerText = "다음 안내 까지 : " + nexGuidDuration.toFixed(1) + "s";
    // document.getElementById('guid-Des-Distance').innerText = pathLeftDistance.toFixed(1) + "m";
    // document.getElementById('guid-Des-Time').innerText = pathLeftDuration.toFixed(1) + "s";

    // console.log("전방 " + visibilityDistance(nextGuidDistacne) + " 에서 " + nextGuid.guidance);
    // console.log("다음 안내까지 : " + visibilityTime(nextGuidDuration));
    // console.log("도착까지  " + visibilityDistance(pathLeftDistance) + " / " + visibilityTime(pathLeftDuration));

    document.getElementById('guid_ance').innerText
        = "전방 " + visibilityDistance(nextGuidDistacne) + " 에서\n" + nextGuid.guidance;
    document.getElementById('guid_left').innerText
        = "도착까지  " + visibilityDistance(pathLeftDistance) + " / " + visibilityTime(pathLeftDuration);

}

function visibilityDistance(dis = 0) {
    if (dis == null)
        return "0m";

    if (Math.floor(dis * 0.001) > 0) {
        return (dis * 0.001).toFixed(1) + "km";
    } else {
        return dis.toFixed(1) + "m";
    }
}

function visibilityTime(dur = 0) {
    if (dur == null)
        return "0분";

    const hours = Math.floor(dur / 3600); // 초를 시간으로 변환
    const minutes = Math.floor((dur % 3600) / 60); // 초를 분으로 변환하고 소수점 첫 번째 자리까지 나타내기

    if (hours > 0) {
        return `${hours}시간${minutes}분`;
    } else {
        return `${minutes}분`;
    }
}

function pathType() {
    return type;
}

// 추가 경로 재생성 동작
function remakeNavi(lat, lng) {
    // setToken();
    //
    // let currectAddress = "";
    //
    // fetch(
    //     'https://dapi.kakao.com/v2/local/geo/coord2address?x=' + lng + '&y=' + lat,
    //     {
    //         headers: {Authorization: 'KakaoAK 4752e5a5b955f574af7718613891f796'}, //rest api 키
    //     }
    // )
    //     .then((response) => response.json())
    //     .then((data) => {
    //         if (data.documents && data.documents.length > 0) {
    //
    //             currectAddress = data.documents[0].address.address_name; // 이
    //
    //             console.warn(currectAddress + " / " + destinationAddress + " OR " + routeData.destinationAddress);
    //             {
    //                 setToken();
    //                 fetch('/api/route?originAddress=' + currectAddress + '&destinationAddress=' + destinationAddress)
    //                     .then(response => response.json())
    //                     .then(data => {
    //                         responseData = data;
    //                         adapt_KakaoResponseToRouteData(data);
    //                         makeLiveMap(data)
    //                         clearNavi();
    //                     })
    //                     .catch(error => {
    //                         alert("경로를 생성할 수 없습니다. 다시 시도해 주세요");
    //                         console.log(error);
    //                         window.location.href = '/view/home';
    //                     });
    //             }
    //
    //         } else {
    //             throw new Error('Could not find address for this coordinates.');
    //         }
    //     });
    setToken();
    fetch('/api/reroute?originY=' + lat + '&originX=' + lng + '&destinationAddress=' + destinationAddress)
        .then(response => response.json())
        .then(data => {
            responseData = data;
            adapt_KakaoResponseToRouteData(data);
            makeLiveMap(data)
            clearNavi();
        })
        .catch(error => {
            alert("경로를 생성할 수 없습니다. 다시 시도해 주세요");
            console.log(error);
            window.location.href = '/view/home';
        });
}

function remakeRandomNavi(lat, lng) {
    // setToken();
    //
    // let currentAddress = "";
    //
    // fetch(
    //     'https://dapi.kakao.com/v2/local/geo/coord2address?x=' + lng + '&y=' + lat,
    //     {
    //         headers: {Authorization: 'KakaoAK 4752e5a5b955f574af7718613891f796'}, //rest api 키
    //     }
    // )
    //     .then((response) => response.json())
    //     .then((data) => {
    //         currentAddress = data.documents[0].address.address_name;
    //         console.log(data);
    //
    //         let destinationLatitude = destinationLocation.lat;
    //         let destinationLongitude = destinationLocation.lng;
    //
    //         if (type === 'save' && offCourseCount === 0) {
    //             // routeData.guides 배열의 맨 마지막 요소가 목적지
    //             let destinationGuide = routeData.guides[routeData.guides.length - 1];
    //             destinationLatitude = destinationGuide.y;
    //             destinationLongitude = destinationGuide.x;
    //         }
    //
    //         console.log(routeData.guides
    //             .filter(guide => guide.type === 1000));
    //
    //         offCourseCount++;
    //
    //         let waypointsX, waypointsY;
    //
    //         waypointsX = "";
    //         waypointsY = "";
    //         let isexist = false;
    //
    //
    //         for (let i = naviInfo_ProcessIndex; i < routeData.guides.length; i++) {
    //             if (routeData.guides[i].type === 1000 && routeData.guides[i].road_index === -1) {
    //                 isexist = true;
    //                 waypointsX += routeData.guides[i].x + " ";
    //                 waypointsY += routeData.guides[i].y + " ";
    //             }
    //         }
    //
    //         if (!isexist) {
    //             waypointsX = "0";
    //             waypointsY = "0";
    //         }
    //
    //         console.log("주소: " + currentAddress);
    //         console.log("경유지 y: " + waypointsY + ", 경유지 x:" + waypointsX);
    //         console.log("목적지 y: " + destinationLatitude + ", 경유지 x:" + destinationLongitude);
    //
    //         {
    //             fetch('/api/offCourse?currentAddress=' + currentAddress
    //                 + '&destinationY=' + destinationLatitude + '&destinationX=' + destinationLongitude
    //                 + '&waypointsY=' + waypointsY + '&waypointsX=' + waypointsX)
    //                 .then(response => response.json())
    //                 .then(data => {
    //                     responseData = data;
    //                     adapt_KakaoResponseToRouteData(data);
    //                     makeLiveMap(data)
    //                     clearNavi();
    //                 })
    //                 .catch(error => {
    //                     alert("경로를 생성할 수 없습니다. 다시 시도해 주세요");
    //                     console.log(error);
    //                     window.location.href = '/view/home';
    //                 });
    //         }
    //     });

    let destinationLatitude = destinationLocation.lat;
    let destinationLongitude = destinationLocation.lng;

    if (type === 'save' && offCourseCount === 0) {
        // routeData.guides 배열의 맨 마지막 요소가 목적지
        let destinationGuide = routeData.guides[routeData.guides.length - 1];
        destinationLatitude = destinationGuide.y;
        destinationLongitude = destinationGuide.x;
    }

    offCourseCount++;

    let waypointsX, waypointsY;

    waypointsX = "";
    waypointsY = "";
    let isexist = false;


    for (let i = naviInfo_ProcessIndex; i < routeData.guides.length; i++) {
        if (routeData.guides[i].type === 1000 && routeData.guides[i].road_index === -1) {
            isexist = true;
            waypointsX += routeData.guides[i].x + " ";
            waypointsY += routeData.guides[i].y + " ";
        }
    }

    if (!isexist) {
        waypointsX = "0";
        waypointsY = "0";
    }

    fetch('/api/offCourse-coordinate?originY=' + lat + '&originX=' + lng
        + '&destinationY=' + destinationLatitude + '&destinationX=' + destinationLongitude
        + '&waypointsY=' + waypointsY + '&waypointsX=' + waypointsX)
        .then(response => response.json())
        .then(data => {
            responseData = data;
            adapt_KakaoResponseToRouteData(data);
            makeLiveMap(data)
            clearNavi();
        })
        .catch(error => {
            alert("경로를 생성할 수 없습니다. 다시 시도해 주세요");
            console.log(error);
            window.location.href = '/view/home';
        });
}

// 마커 표시하기
function makeMarker(data) {
    // 출발지 도착지 마커 표시하기
    var let_ori = data.routes[0].summary.origin.y;
    var lon_ori = data.routes[0].summary.origin.x;
    var lat_des = data.routes[0].summary.destination.y;
    var lon_des = data.routes[0].summary.destination.x;

    var positions = [
        {
            title: '출발',
            latlng: new kakao.maps.LatLng(let_ori, lon_ori),
            image: 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/red_b.png'
            //"https://cdn-icons-png.flaticon.com/512/6213/6213694.png"
        },
        {
            title: '도착',
            latlng: new kakao.maps.LatLng(lat_des, lon_des),
            image: 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/blue_b.png'
            //"https://cdn-icons-png.flaticon.com/512/4856/4856582.png"
        }

    ]
// 마커를 지도에 표시하기
    for (var i = 0; i < positions.length; i++) {
        // 마커 이미지 크기
        var imageSize = new kakao.maps.Size(30, 40);
        var markerImage = new kakao.maps.MarkerImage(positions[i].image, imageSize);
        var marker = new kakao.maps.Marker({
            map: map,
            position: positions[i].latlng,
            title: positions[i].title,
            image: markerImage,
        })
    }

// 경유지 마커 표시
    var imageWay = 'https://cdn-icons-png.flaticon.com/128/4198/4198066.png';
    var imageSize = new kakao.maps.Size(40, 35);
    // 마커 이미지를 생성합니다
    var markerImage = new kakao.maps.MarkerImage(imageWay, imageSize);

    // 경유지 마커 표시하기
    for (var i = 0; i < data.routes[0].sections.length; i++) {
        var section = data.routes[0].sections[i];
        for (var j = 0; j < section.guides.length; j++) {
            var guide = section.guides[j];
            if (guide.type === 1000) {
                var lat_way = guide.y;
                var lon_way = guide.x;

                var latlng = new kakao.maps.LatLng(lat_way, lon_way)

                var marker = new kakao.maps.Marker({
                    map: map,
                    position: latlng,
                    image: markerImage
                });
            }
        }
    }
}

// 히스토리 마커 만들기
// 마커 표시하기
function historiesMakeMarker(data) {
    // 히스토리 마커용 추가
    for (var i = 0; i < data.guides.length; i++) {
        if (data.guides[i].type === 100) {
            var lat_ori = data.guides[i].y;
            var lng_ori = data.guides[i].x;
        }

        if (data.guides[i].type === 101) {
            var lat_des = data.guides[i].y;
            var lng_des = data.guides[i].x;
        }
        var positions = [
            {
                title: '출발',
                latlng: new kakao.maps.LatLng(lat_ori, lng_ori),
                image: 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/red_b.png'
                //"https://cdn-icons-png.flaticon.com/512/6213/6213694.png"
            },
            {
                title: '도착',
                latlng: new kakao.maps.LatLng(lat_des, lng_des),
                image: 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/blue_b.png'
                //"https://cdn-icons-png.flaticon.com/512/4856/4856582.png"
            }

        ]

        // 마커를 지도에 표시하기
        for (var i = 0; i < positions.length; i++) {
            // 마커 이미지 크기
            var imageSize = new kakao.maps.Size(30, 40);
            var markerImage = new kakao.maps.MarkerImage(positions[i].image, imageSize);
            var marker = new kakao.maps.Marker({
                map: map,
                position: positions[i].latlng,
                title: positions[i].title,
                image: markerImage,
            })

        }

    }

//     // 출발지 도착지 마커 표시하기
//     for (var i = 0; i < data.guides.length; i++) {
//         var guide = guides[i];
//         if (guide.type === 100) {
//             var lat_ori = guide.y;
//             var lon_ori = guide.x;
//         }
//
//         if (guide.type === 101) {
//             var lat_des = guide.y;
//             var lon_des = guide.x;
//         }
//     }
//
//     var positions = [
//         {
//             title: '출발',
//             latlng: new kakao.maps.LatLng(lat_ori, lon_ori),
//             image: 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/red_b.png'
//             //"https://cdn-icons-png.flaticon.com/512/6213/6213694.png"
//         },
//         {
//             title: '도착',
//             latlng: new kakao.maps.LatLng(lat_des, lon_des),
//             image: 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/blue_b.png'
//             //"https://cdn-icons-png.flaticon.com/512/4856/4856582.png"
//         }
//
//     ]
// // 마커를 지도에 표시하기
//     for (var i = 0; i < positions.length; i++) {
//         // 마커 이미지 크기
//         var imageSize = new kakao.maps.Size(30, 40);
//         var markerImage = new kakao.maps.MarkerImage(positions[i].image, imageSize);
//         var marker = new kakao.maps.Marker({
//             map: map,
//             position: positions[i].latlng,
//             title: positions[i].title,
//             image: markerImage,
//         })
//     }
//
// // 경유지 마커 표시
//     var imageWay = 'https://file.notion.so/f/f/0bb6a7f0-5b10-43e2-ad69-0657263c6dff/ccc1ee90-0fa2-4d98-a1a6-80f40600896f/%EA%B2%BD%EC%9C%A0%EC%A7%80-01.png?id=6eaca2df-ea86-468a-bf07-51df643bf11b&table=block&spaceId=0bb6a7f0-5b10-43e2-ad69-0657263c6dff&expirationTimestamp=1698933600000&signature=256uSQ0yL4SEQ_vhA4Ejx0_PTykB_Nj-2KJev6apoOI&downloadName=%EA%B2%BD%EC%9C%A0%EC%A7%80-01.png';
//     var imageSize = new kakao.maps.Size(40, 35);
//     // 마커 이미지를 생성합니다
//     var markerImage = new kakao.maps.MarkerImage(imageWay, imageSize);
//
//     // 경유지 마커 표시하기
//     for (var j = 0; j < data.guides.length; j++) {
//         var guide = guides[j];
//         if (guide.type === 1000) {
//             var lat_way = guide.y;
//             var lon_way = guide.x;
//
//             var latlng = new kakao.maps.LatLng(lat_way, lon_way)
//
//             var marker = new kakao.maps.Marker({
//                 map: map,
//                 position: latlng,
//                 image: markerImage
//             });
//         }
//     }

}

function saveNavi() {
    if (type === 'live') {
        saveRoute(responseData, originAddress, destinationAddress)
    } else if (type === 'live-random') {
        saveRoute(responseData, originAddress, destinationAddress)
    } else if (type === 'live-all-random') {
        saveRoute(responseData, originAddress, '무작위 주소')
    } else {
        return;
    }
}