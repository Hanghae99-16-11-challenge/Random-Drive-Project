


// 출발지 주소 검색 버튼----------------------------------------------------------------------------------------------------//
document.getElementById('search-origin').addEventListener('click', function() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('originAddress').value = data.address;
        }
    }).open();
});

// 도착지 주소 검색 버튼----------------------------------------------------------------------------------------------------//
document.getElementById('search-destination').addEventListener('click', function() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('destinationAddress').value = data.address;
        }
    }).open();
});

// 사용자가 길 찾기 버튼을 눌렀을 때의 동작----------------------------------------------------------------------------------------------------//
document.getElementById('search-form').addEventListener('submit', function(e) {
    e.preventDefault(); // 기본 submit 동작을 막습니다.

    var originAddress = document.getElementById('originAddress').value;
    var destinationAddress = document.getElementById('destinationAddress').value;

    fetch('/route?originAddress=' + originAddress  + '&destinationAddress=' + destinationAddress)
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







