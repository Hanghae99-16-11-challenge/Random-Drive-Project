var pathData;//마지막 길 찾은 정보 - 네비게이션 디버깅에 쓰임

var positionMark = new kakao.maps.Marker();
var positionText = new kakao.maps.InfoWindow();
var naviInfoMark = new kakao.maps.Marker();
var naviInfoText = new kakao.maps.InfoWindow();

var naviInfo_SectionIndex = 0;
var naviInfo_GuidIndex = 1;

//0 : 이미 도착지점일때 , -1 : 유효하지 않은 경로일때 , 1 : 성공 , 2 : 구간 시작점, 3 : 다음이 도착지점일때
var naviInfo_State = -1;


let intervalId;

function startCorutine()
{
    intervalId = setInterval(function () {
        navigator.geolocation.getCurrentPosition(function(position) {
            // 위치 정보를 표시하기
            // $("#location2").text("\n, GPS 위치 정보: " + position.coords.latitude + ", " + position.coords.longitude);
            EditMark(positionMark, positionText, position.coords.latitude, position.coords.longitude, '현위치');
            console.log("위치 업데이트");
        });
    }, 1000);
}
function stopCoroutine()
{
    clearInterval(intervalId);
}

