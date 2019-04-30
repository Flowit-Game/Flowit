var currentQuery = location.search;

function findGetParameter(parameterName) {
    var result = null,
        tmp = [];
    var items = currentQuery.substr(1).split("&");
    for (var index = 0; index < items.length; index++) {
        tmp = items[index].split("=");
        if (tmp[0] === parameterName) result = decodeURIComponent(tmp[1]);
    }
    return result;
}

function param(paramName, offset) {
    return Number(findGetParameter(paramName)) + offset;
}

function posArg() {
    return "c=" + findGetParameter("c") + "&r=" + findGetParameter("r");
}

function api(url) {
    url = url.replace("./?", "./api.php?");
    $.get(url, function(data) {
        currentQuery = url.replace("./api.php", "");
        element = $.parseHTML(data);
        $("#board").replaceWith(element);
        updateToolbox();
        updateSolved();
    });
}

document.onkeydown = checkKey;
function checkKey(e) {
    e = e || window.event;
    if (e.keyCode == '38') {
        // up arrow
        api("./?action=edit&c="+param("c",  0)+"&r="+param("r", -1));
        e.preventDefault();
    } else if (e.keyCode == '40') {
        // down arrow
        api("./?action=edit&c="+param("c",  0)+"&r="+param("r", +1));
        e.preventDefault();
    } else if (e.keyCode == '37') {
       // left arrow
        api("./?action=edit&c="+param("c", -1)+"&r="+param("r",  0));
        e.preventDefault();
    } else if (e.keyCode == '39') {
       // right arrow
        api("./?action=edit&c="+param("c", +1)+"&r="+param("r",  0));
        e.preventDefault();
    }

}

function updateToolbox() {
    $(".toolbox-item").hide();
    type = $("#board").data("selected-type");
    $(".toolbox-item-" + type).show();
}

function updateSolved() {
    solved = $("#board").data("solved");
    if (solved) {
        $("#solvedCongrats").show(500);
        $("#saveButton").removeClass("btn-secondary").addClass("btn-success");
    } else {
        $("#saveButton").removeClass("btn-success").addClass("btn-secondary");
    }
}
    
