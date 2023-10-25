$(document).ready(function() {
    var animTime = 300;
    var delX;
    var delY;
    var phWid = parseInt($(".photo").css("width"));
    var numOfPhotos = $(".photo").length;
    var topCard = "div[ph-index='0']";
    var curPhoto = 1;
    var shift;
    var k = parseInt($("html").css("font-size")) / 16;
    var busy = false;
    var startedDragging = false;

    for (i = 0; i <= (numOfPhotos - 1); i++) {
        loadBackground(".photo-" + i);
    }

    changeCounter();
    rearrangeStack();

    function changeCounter(nope) {
        if (!nope) {
            $(".photo-counter").html(curPhoto + "/" + numOfPhotos);
            if (curPhoto != numOfPhotos)
                curPhoto++;
            else
                curPhoto = 1;
        } else
            $(".photo-counter").html((curPhoto - 1) + "/" + numOfPhotos);
    }

    function handleAnimation() {
        busy = true;
        $(".photo").addClass("animation");
        setTimeout(function() {
            $(".photo").removeClass("animation");
            busy = false;
            startedDragging = false;
        }, animTime);
    }

    function rearrangeStack() {
        handleAnimation();

        setTimeout(function() {

            for (i = 0; i < numOfPhotos; i++)
                $("div[ph-index=" + i + "]").css({
                    "z-index": numOfPhotos - i - 1
                });

            if (numOfPhotos != 1)
                $("div[ph-index=" + (numOfPhotos - 1) + "]").css(
                    "transform", "translate3d(0, " + 0.65 * (numOfPhotos - 1) + "rem, 0)" + " scale(" + (1 - (numOfPhotos - 1) * 0.05) + ")"
                );

        }, animTime);

        if (numOfPhotos != 1)
            $("div[ph-index=" + (numOfPhotos - 1) + "]").css({
                "opacity": 0,
                "transform": "translate3d" + shift
            }).children().removeClass("ph-info-active");
        else
            $("div[ph-index='0']").css("transform", "none");

        $("div[ph-index='0']").children().addClass("ph-info-active");

        $(".bg-pic").removeClass("bg-active");
        var n = $("div[ph-index='0']").attr("number");
        $(".bg-pic-" + n).addClass("bg-active");

        for (i = 0; i < numOfPhotos - 1; i++) {
            if (i >= 5) var op = 0;
            else var op = 1;
            $("div[ph-index=" + i + "]").css({
                "transform": "translate3d(0, " + 0.65 * i + "rem, 0)" + " scale(" + (1 - i * 0.05) + ")",
                "opacity": op
            });
        }
    }

    function reindexate() {
        for (i = 0; i < numOfPhotos; i++)
            $("div[ph-index=" + i + "]").attr("ph-index", i - 1);
        $("div[ph-index='-1']").attr("ph-index", numOfPhotos - 1);
        changeCounter();
    }

    function loadBackground(selector) {
        var url = $(selector).css("background-image");
        var bgIndex = $(selector).attr("ph-index");
        $(".phone-screen").append("<div class='bg-pic bg-pic-" + $(selector).attr("number") + "'></div>");
        $(".bg-pic").last().css("background-image", url);
    }

    $(".photo-container").on("mousedown touchstart", function(event) {
        if (busy)
            return;
        startedDragging = true;
        if (!event.pageX) event.preventDefault();
        var stX = event.pageX || event.originalEvent.touches[0].pageX;
        var stY = event.pageY || event.originalEvent.touches[0].pageY;
        $(".phone-screen").on("mousemove touchmove", function(event) {
            delX = (event.pageX || event.originalEvent.touches[0].pageX) - stX;
            delY = (event.pageY || event.originalEvent.touches[0].pageY) - stY;
            var rotY = delX / phWid;
            var rotX = delY / phWid;
            $(topCard).css("transform", "rotateY(" + rotY * 25 + "deg)" + " rotateX(" + rotX * (-25) + "deg)" + " translate3d(" + delX / 5 * k + "px, " + delY / 5 * k + "px, 0)");
        });
    });

    $(".phone-screen").on("mouseleave", function() {
        $(document).trigger("mouseup");
        delX = 0;
        delY = 0;
    });

    $(document).on("mouseup touchend", function() {
        if (!startedDragging)
            return;
        $(".phone-screen").off("mousemove touchmove");
        if (delX > delY && delX > phWid / 2) {
            shift = "(" + phWid * 1.5 + "px, 0, 0)";
            reindexate();
        } else if (delX < delY && delY > phWid / 2) {
            shift = "(0, " + phWid * 1.5 + "px, 0)";
            reindexate();
        } else if (Math.abs(delX) > Math.abs(delY) && delX < -phWid / 2) {
            shift = "(" + (-phWid * 1.5) + "px, 0, 0)";
            reindexate();
        } else if (Math.abs(delX) < Math.abs(delY) && delY < -phWid / 2) {
            shift = "(0, " + (-phWid * 1.5) + "px, 0)";
            reindexate();
        }
        delX = 0;
        delY = 0;
        rearrangeStack();
    });
});