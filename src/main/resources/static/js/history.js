const host = 'http://' + window.location.host;
// Header에서 Token 가져오기
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
// 상세 정보 표시
$(document).ready(function() {
    const url = window.location.href;
    const routeId = url.substring(url.lastIndexOf("/") + 1);

    fetch('/api/route/' + routeId)
        .then(response => response.json())
        .then(data => {
            if (!map) {
                map = new kakao.maps.Map(document.getElementById('map'), {
                    level: 3
                });
            }
            let bound = data.bounds;

            let bounds = new kakao.maps.LatLngBounds(
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

                polylines_his.push(polyline);
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
});


// kakaomap 표시 해주는 곳-----------------------------------------------------------------------------------------------------//
let container = document.getElementById('map');
let options = {
    center: new kakao.maps.LatLng(33.450701, 126.570667),
    level: 3
};

let map = new kakao.maps.Map(container, options);
// 경로 안내 polyline ----------------------------------------------------------------------------------------------------------//
let polylines_his = [];
function clearpolylines_his() {
    for (let i = 0; i < polylines_his.length; i++) {
        polylines_his[i].setMap(null);
    }
    polylines_his = [];
}
