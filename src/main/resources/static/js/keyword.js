$('#all-random-search-keyword').on('click', function() {
    $('#keyword-modal').show();
});

$('.close-btn').on('click', function() {
    $('#keyword-modal').hide();
});

// API 호출하여 키워드로 도로명 검색하기
$('#keyword-search-btn').on('click', function() {
    var keyword = $('#keyword-input').val();
    searchKeyword(keyword);
});

function searchKeyword(keyword) {
    $.ajax({
        url: "/api/keyword-random-route",
        data: {
            query: keyword
        },
        success: function(response) {
            displayResults(response);
        },
        error: function(error) {
            console.error(error);
        }
    });
}

// 모달에 검색 결과 표시하기
function displayResults(response) {
    var $results = $('<ul>');

    // 모달 내용 초기화
    $('.modal-content ul').remove();

    // 응답의 첫 번째 배열은 장소 이름, 두 번째 배열은 주소 이름입니다.
    for (var i = 0; i < response[0].length; i++) {
        (function(index) {
            var placeName = response[0][index];
            var addressName = response[1][index];
            var $li = $('<li>').text(placeName + ", " + addressName);
            $li.on('click', function() {
                $('#all-random-originAddress').val(addressName);
                $('#keyword-modal').hide();
            });
            $results.append($li);
        })(i);
    }
    $('.modal-content').append($results);
}

// 목적지 기반-------------------------------------------------------//
$('#all-random-search-keyword-destination').on('click', function() {
    $('#keyword-modal-destination').show();
});

$('.close-btn').on('click', function() {
    $('#keyword-modal-destination').hide();
});

// API 호출하여 키워드로 도로명 검색하기
$('#keyword-search-btn-destination').on('click', function() {
    var keyword = $('#keyword-input-destination').val();
    destinationSearchKeyword(keyword);
});

function destinationSearchKeyword(keyword) {
    $.ajax({
        url: "/api/keyword-random-route",
        data: {
            query: keyword
        },
        success: function(response) {
            destinationDisplayResults(response);
        },
        error: function(error) {
            console.error(error);
        }
    });
}

// 모달에 검색 결과 표시하기
function destinationDisplayResults(response) {
    var $results = $('<ul>');

    // 모달 내용 초기화
    $('.modal-content ul').remove();

    // 응답의 첫 번째 배열은 장소 이름, 두 번째 배열은 주소 이름입니다.
    for (var i = 0; i < response[0].length; i++) {
        (function(index) {
            var placeName = response[0][index];
            var addressName = response[1][index];
            var $li = $('<li>').text(placeName + ", " + addressName);
            $li.on('click', function() {
                $('#random-destinationAddress').val(addressName);
                $('#keyword-modal-destination').hide();
            });
            $results.append($li);
        })(i);
    }
    $('.modal-content').append($results);
}