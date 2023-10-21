const host = 'http://' + window.location.host;
$(document).ready(function() {
    const url = window.location.href;
    const segments = url.split("/");
    const type = segments[segments.length - 5]; // 뒤에서 다섯 번째 segment
    const routeId = segments[segments.length - 4]; // 뒤에서 네 번째 segment
    const originAddress = segments[segments.length - 3]; // 뒤에서 세 번째 segment
    const destinationAddress = segments[segments.length - 2]; // 뒤에서 두 번째 segment
    const redius = segments[segments.length - 1]; // 마지막 segment
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
            // "아니오" 버튼 생성
            let noButton = document.createElement('button');
            noButton.textContent = '아니오';
            document.getElementById('yes-or-no').insertAdjacentElement('afterend', noButton);
            noButton.addEventListener('click', function() {
                // 여기에 "아니오" 버튼을 눌렀을 때 실행할 동작 추가 가능
                window.location.href = '/api/home';
                confirmationMessage.remove(); // 문구와 버튼 제거
                yesButton.remove();
                noButton.remove();
            });

            // "예" 버튼 생성
            let yesButton = document.createElement('button');
            yesButton.textContent = '예';
            document.getElementById('yes-or-no').insertAdjacentElement('afterend', yesButton);
            yesButton.addEventListener('click', function() {
                // 여기에 "예" 버튼을 눌렀을 때 실행할 동작 추가 가능

                confirmationMessage.remove(); // 문구와 버튼 제거
                yesButton.remove();
                noButton.remove();
            });

            // "해당 경로로 안내해 드릴까요?" 문구 표시
            let confirmationMessage = document.createElement('p');
            confirmationMessage.textContent = '해당 경로로 안내해 드릴까요?';
            document.getElementById('yes-or-no').insertAdjacentElement('afterend', confirmationMessage);
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
            makeLiveMap(data, originAddress, destinationAddress)
        });
}

// 목적지 기반 랜덤 길찾기 동작
function makeRandomNavi(originAddress, destinationAddress, redius) {
    setToken();
    fetch('/random-route?originAddress=' + originAddress  + '&destinationAddress=' + destinationAddress + '&redius=' + redius)
        .then(response => response.json())
        .then(data => {
            makeLiveMap(data, originAddress, destinationAddress)
        });
}

// 반경 기반 랜덤 길찾기 동작
function makeAllRandomNavi(originAddress, redius) {
    setToken();
    fetch('/all-random-route?originAddress=' + originAddress  + '&redius=' + redius)
        .then(response => response.json())
        .then(data => {
            makeLiveMap(data, originAddress, "무작위 목적지")
        });
}


// 신규 길찾기, 랜덤 길찾기에서 얻은 response를 가공해서 Map에 띄워줌
function makeLiveMap(data, originAddress, destinationAddress) {
    clearPolylines(); // 기존의 선들을 모두 제거
    if (!map) {
        map = new kakao.maps.Map(document.getElementById('map'), {
            level: 3
        });
    }

    // 경로 정보(routes)의 각 섹션(section)별로 반복하여 처리합니다.
    for (let route of data.routes) {
        for (let section of route.sections) {

            // 각 섹션의 경계 상자(bound) 정보를 가져옵니다.
            let bound = section.bound;

            // 카카오 지도에 섹션을 표시합니다.
            var bounds = new kakao.maps.LatLngBounds(
                new kakao.maps.LatLng(bound.min_y, bound.min_x),
                new kakao.maps.LatLng(bound.max_y, bound.max_x)
            );

            map.setBounds(bounds);

            // polyline 생성
            for(let road of section.roads){
                let path = [];
                for(let i=0; i<road.vertexes.length; i+=2){
                    console.log("vertexes: ", road.vertexes[i], road.vertexes[i+1]);
                    path.push(new kakao.maps.LatLng(road.vertexes[i+1], road.vertexes[i]));
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
    // "아니오" 버튼 생성
    let noButton = document.createElement('button');
    noButton.textContent = '아니오';
    document.getElementById('yes-or-no').insertAdjacentElement('afterend', noButton);
    noButton.addEventListener('click', function() {
        // 여기에 "아니오" 버튼을 눌렀을 때 실행할 동작 추가 가능
        confirmationMessage.remove(); // 문구와 버튼 제거
        yesButton.remove();
        noButton.remove();
    });

    // "예" 버튼 생성
    let yesButton = document.createElement('button');
    yesButton.textContent = '예';
    document.getElementById('yes-or-no').insertAdjacentElement('afterend', yesButton);
    yesButton.addEventListener('click', function() {

        fetch('/api/routes', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': auth // 인증 토큰을 Authorization 헤더에 추가
            },
            body: JSON.stringify({
                requestData: data, // KakaoRouteAllResponseDto 객체
                originAddress: originAddress,
                destinationAddress: destinationAddress
            })
        })

        confirmationMessage.remove(); // 문구와 버튼 제거
        yesButton.remove();
        noButton.remove();
    });

    // "해당 경로로 안내해 드릴까요?" 문구 표시
    let confirmationMessage = document.createElement('p');
    confirmationMessage.textContent = '해당 경로로 안내해 드릴까요?';
    document.getElementById('yes-or-no').insertAdjacentElement('afterend', confirmationMessage);
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