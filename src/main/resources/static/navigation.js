function handleCurrentLocationClick() {
    navigator.geolocation.getCurrentPosition(function(position) {
        var lat = position.coords.latitude,
            lon = position.coords.longitude;

        fetch(
            'https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=' + lon + '&y=' + lat,
            {
                headers: { Authorization: 'KakaoAK 4752e5a5b955f574af7718613891f796' },
            }
        )
            .then((response) => response.json())
            .then((data) => {
                if (data.documents && data.documents.length > 0) {
                    document.getElementById('originAddress').value = data.documents[0].address_name;
                } else {
                    throw new Error('Could not find address for this coordinates.');
                }
            });
    });
}
document.getElementById('current-location').addEventListener('click', handleCurrentLocationClick);
document.getElementById('random-current-location').addEventListener('click', handleCurrentLocationClick);
document.getElementById('all-random-current-location').addEventListener('click', handleCurrentLocationClick);

// 출발지 주소 검색 버튼----------------------------------------------------------------------------------------------------//
document.getElementById('search-origin').addEventListener('click', function() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('originAddress').value = data.address;
        }
    }).open();
});

// 출발지 주소 검색 버튼----------------------------------------------------------------------------------------------------//
document.getElementById('search-origin').addEventListener('click', function() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('originAddress').value = data.address;
        }
    }).open();
});
// 목적지 주소 검색 버튼----------------------------------------------------------------------------------------------------//
document.getElementById('search-destination').addEventListener('click', function() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('destinationAddress').value = data.address;
        }
    }).open();
});

// 반경 기반 랜덤 길찾기 출발지 주소 검색 버튼----------------------------------------------------------------------------------------------------//
document.getElementById('all-random-search-origin').addEventListener('click', function() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('all-random-originAddress').value = data.address;
        }
    }).open();
});

// 목적지,반경 기반 랜덤 길찾기 출발지 주소 검색 버튼----------------------------------------------------------------------------------------------------//
document.getElementById('random-search-origin').addEventListener('click', function() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('random-originAddress').value = data.address;
        }
    }).open();
});
// 목적지,반경 기반 랜덤 길찾기 목적지 주소 검색 버튼----------------------------------------------------------------------------------------------------//
document.getElementById('random-search-destination').addEventListener('click', function() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('random-destinationAddress').value = data.address;
        }
    }).open();
});
// 사용자가 반경기반 랜덤 길 찾기 버튼을 눌렀을 때의 동작----------------------------------------------------------------------------------------------------//
document.getElementById('all-random-search-form').addEventListener('submit', function(e) {
    e.preventDefault(); // 기본 submit 동작을 막습니다.

    var originAddress = document.getElementById('all-random-originAddress').value;
    var redius = document.getElementById('all-random-redius').value;

    fetch('/all-random-route?originAddress=' + originAddress  + '&redius=' + redius)
        .then(response => response.json())
        .then(data => {
            // data는 KakaoRouteAllResponseDto 객체
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
        });
});
// 사용자가 목적지기반 랜덤 길 찾기 버튼을 눌렀을 때의 동작----------------------------------------------------------------------------------------------------//
document.getElementById('random-search-form').addEventListener('submit', function(e) {
    e.preventDefault(); // 기본 submit 동작을 막습니다.

    var originAddress = document.getElementById('random-originAddress').value;
    var destinationAddress = document.getElementById('random-destinationAddress').value;
    var redius = document.getElementById('random-redius').value;

    fetch('/random-route?originAddress=' + originAddress  + '&destinationAddress=' + destinationAddress + '&redius=' + redius)
        .then(response => response.json())
        .then(data => {
            // data는 KakaoRouteAllResponseDto 객체
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
        });
});

// kakaomap 표시 해주는 곳-----------------------------------------------------------------------------------------------------//
var container = document.getElementById('map');
var options = {
    center: new kakao.maps.LatLng(33.450701, 126.570667),
    level: 3
};

var map = new kakao.maps.Map(container, options);

// 상세교통상황 표시------------------------------------------------------------------------------------------------------------//
// map.addOverlayMapTypeId(kakao.maps.MapTypeId.TRAFFIC);

// 경로 안내 polyline ----------------------------------------------------------------------------------------------------------//
var polylines = [];
function clearPolylines() {
    for (let i = 0; i < polylines.length; i++) {
        polylines[i].setMap(null);
    }
    polylines = [];
}

// 현재 위치 마커 표시----------------------------------------------------------------------------------------------------------//
// HTML5의 geolocation으로 사용할 수 있는지 확인합니다
if (navigator.geolocation) {

    // GeoLocation을 이용해서 접속 위치를 얻어옵니다
    navigator.geolocation.getCurrentPosition(function(position) {

        var lat = position.coords.latitude, // 위도
            lon = position.coords.longitude; // 경도

        var locPosition = new kakao.maps.LatLng(lat, lon), // 마커가 표시될 위치를 geolocation으로 얻어온 좌표로 생성합니다
            message = '<div style="padding:5px;">여기에 계신가요?!</div>'; // 인포윈도우에 표시될 내용입니다

        // 마커와 인포윈도우를 표시합니다
        displayMarker(locPosition, message);

    });

} else { // HTML5의 GeoLocation을 사용할 수 없을때 마커 표시 위치와 인포윈도우 내용을 설정합니다

    var locPosition = new kakao.maps.LatLng(33.450701, 126.570667),
        message = 'geolocation을 사용할수 없어요..'

    displayMarker(locPosition, message);
}

// 지도에 마커와 인포윈도우를 표시하는 함수입니다
function displayMarker(locPosition, message) {

    // 마커를 생성합니다
    var marker = new kakao.maps.Marker({
        map: map,
        position: locPosition
    });

    var iwContent = message, // 인포윈도우에 표시할 내용
        iwRemoveable = true;

    // 인포윈도우를 생성합니다
    var infowindow = new kakao.maps.InfoWindow({
        content : iwContent,
        removable : iwRemoveable
    });

    // 인포윈도우를 마커위에 표시합니다
    infowindow.open(map, marker);

    // 지도 중심좌표를 접속위치로 변경합니다
    map.setCenter(locPosition);
}