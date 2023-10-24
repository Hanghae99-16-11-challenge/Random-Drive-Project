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
var map = new kakao.maps.Map(container, options);

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
                    console.log("vertexes: ", road.vertexes[i], road.vertexes[i+1]);
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