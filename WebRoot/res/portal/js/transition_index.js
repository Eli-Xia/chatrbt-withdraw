$(function () {
    chatTransitionA();
    adTransitionB();
});

function chatTransitionA() {
    $("#chat .sign").animate({
        left: '100px'
    }, 200, function () {
        $("#chat #demo_2").show();
        $("#chat .sign").animate({
            left: '100px'
        }, 600, function () {
            $("#chat #demo_3").show();
            $("#chat .sign").animate({
                left: 0
            }, 1000, function () {
                $("#chat .mouse").show();
                $("#chat .mouse").animate({
                    right: "140px",
                    top: '74px'
                }, 400, function () {
                    $("#chat .mouse").animate({
                        "width": "26px",
                        "height": "26px",
                        'margin-top': "1px",
                        'margin-right': '1px'
                    }, 150, function () {
                        $("#chat .mouse").animate({
                            width: "36px",
                            height: "36px",
                            marginTop: "-4px",
                            marginRight: "-4px",
                            opacity: 0
                        }, 400, function () {
                            $("#chat .mouse").hide();
                            $("#chat .mouse").css({
                                "width": "28px",
                                "height": "28px",
                                'opacity': "1",
                                'margin-top': 0,
                                'margin-right': 0
                            })
                            chatTransitionB()
                        })
                    });
                })
            })
        })
    })
}

function chatTransitionB() {
    $("#chat .demo").animate({
        left: "-207px"
    }, 400, function () {
        $("#chat .sign").animate({
            left: '100px'
        }, 800, function () {
            $("#chat .mouse").show();
            $("#chat .mouse").animate({
                right: "168px",
                top: '10px'
            }, 400, function () {
                $("#chat .mouse").animate({
                    "width": "26px",
                    "height": "26px",
                    'margin-top': "1px",
                    'margin-right': '1px'
                }, 150, function () {
                    $("#chat .mouse").animate({
                        width: "36px",
                        height: "36px",
                        marginTop: "-4px",
                        marginRight: "-4px",
                        opacity: 0
                    }, 600, function () {
                        $("#chat .mouse").hide();
                        $("#chat .mouse").css({
                            "width": "28px",
                            "height": "28px",
                            'opacity': "1",
                            'right': 0,
                            'top': '300px',
                            'margin-top': 0,
                            'margin-right': 0
                        })
                        $("#chat #demo_2").hide();
                        $("#chat #demo_3").hide();
                        $("#chat .demo").css({"left": 0});
                        $("#chat .sign").css({"left": 0});
                        chatTransitionA()
                    })
                })
            })
        })
    })
}

function adTransitionA() {
    $("#ad .demo").animate({
        left: '-207px'
    }, 600, function () {
        $("#ad .sign").animate({
            left: '100px'
        }, 1200, function () {
            $("#ad .mouse").show();
            $("#ad .mouse").animate({
                right: "168px",
                top: '10px'
            }, 400, function () {
                $("#ad .mouse").animate({
                    "width": "26px",
                    "height": "26px",
                    'margin-top': "1px",
                    'margin-right': '1px'
                }, 150, function () {
                    $("#ad .mouse").animate({
                        width: "36px",
                        height: "36px",
                        marginTop: "-4px",
                        marginRight: "-4px",
                        opacity: 0
                    }, 400, function () {
                        $("#ad .mouse").hide();
                        $("#ad .mouse").css({
                            "width": "28px",
                            "height": "28px",
                            'opacity': "1",
                            'right': 0,
                            'top': '308px',
                            'margin-top': 0,
                            'margin-right': 0
                        })
                        adTransitionB();
                    })
                })
            })
        })
    })
}

function adTransitionB() {
    $("#ad .demo").animate({
        left: "0"
    }, 600, function () {
        $("#ad .sign").animate({
            left: 0
        }, 1200, function () {
            $("#ad .mouse").show();
            $("#ad .mouse").animate({
                right: "122px",
                top: '220px'
            }, 400, function () {
                $("#ad .mouse").animate({
                    "width": "26px",
                    "height": "26px",
                    'margin-top': "1px",
                    'margin-right': '1px'
                }, 150, function () {
                    $("#ad .mouse").animate({
                        width: "36px",
                        height: "36px",
                        marginTop: "-4px",
                        marginRight: "-4px",
                        opacity: 0
                    }, 400, function () {
                        $("#ad .mouse").hide();
                        $("#ad .mouse").css({
                            "width": "28px",
                            "height": "28px",
                            'opacity': "1",
                            'right': "100px",
                            'top': '150px',
                            'margin-top': 0,
                            'margin-right': 0
                        })
                        adTransitionA();
                    })
                })
            })
        })
    })
}