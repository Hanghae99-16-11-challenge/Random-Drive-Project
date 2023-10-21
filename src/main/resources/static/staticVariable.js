var pathData;//마지막 길 찾은 정보 - 네비게이션 디버깅에 쓰임

var positionMark = new kakao.maps.Marker();
var positionText = new kakao.maps.InfoWindow();
var naviInfoMark = new kakao.maps.Marker();
var naviInfoText = new kakao.maps.InfoWindow();

var naviInfo_SectionIndex = 0;
var naviInfo_GuidIndex = 1;

//0 : 이미 도착지점일때 , -1 : 유효하지 않은 경로일때 , 1 : 성공 , 2 : 구간 시작점, 3 : 다음이 도착지점일때
var naviInfo_State = -1;
