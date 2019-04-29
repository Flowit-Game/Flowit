function removeOnclick() {
        document.onkeydown = function(e) {
            e.preventDefault();
        };
}

function findGetParameter(parameterName) {
    var result = null,
        tmp = [];
    var items = location.search.substr(1).split("&");
    for (var index = 0; index < items.length; index++) {
        tmp = items[index].split("=");
        if (tmp[0] === parameterName) result = decodeURIComponent(tmp[1]);
    }
    return result;
}

function param(paramName, offset) {
    return Number(findGetParameter(paramName)) + offset;
}

document.onkeydown = checkKey;
function checkKey(e) {
    e = e || window.event;
    if (e.keyCode == '38') {
        // up arrow
        window.location.href="./?action=edit&c="+param("c",  0)+"&r="+param("r", -1);
        removeOnclick();
        e.preventDefault();
    } else if (e.keyCode == '40') {
        // down arrow
        window.location.href="./?action=edit&c="+param("c",  0)+"&r="+param("r", +1);
        removeOnclick();
        e.preventDefault();
    } else if (e.keyCode == '37') {
       // left arrow
        window.location.href="./?action=edit&c="+param("c", -1)+"&r="+param("r",  0);
        removeOnclick();
        e.preventDefault();
    } else if (e.keyCode == '39') {
       // right arrow
        window.location.href="./?action=edit&c="+param("c", +1)+"&r="+param("r",  0);
        removeOnclick();
        e.preventDefault();
    }

}
function createCookie(name,value,days) {
    if (days) {
        var date = new Date();
        date.setTime(date.getTime()+(days*24*60*60*1000));
        var expires = "; expires="+date.toGMTString();
    }
    else {var expires = ""; }
    document.cookie = name+"="+value+expires+"; path=/";
}

function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}

function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
      var pair = vars[i].split("=");
      if (pair[0] == variable) {
        return pair[1];
      }
    }
  }

function clickRandom(cssClass) {
    var links = $("a."+cssClass);
    if(links.length == 0) {
        autostart();
        return;
    }
    var randomNumber = Math.floor(Math.random()*links.length)  
    links.get(randomNumber).click();
}
function startRandom() {
    if (!confirm("Start random?")) {
        return;
    }
    createCookie("randClicks", 60, 1);
    clickRandom();
}
function autostart() {
    if(readCookie("randClicks") > 0) {
        createCookie("randClicks", readCookie("randClicks") - 1, 1);
        var click = "field";
        if(getQueryVariable("action") == "edit" ||
                getQueryVariable("action") == "save_type" ||
                getQueryVariable("action") == "save_color" ||
                getQueryVariable("action") == "save_preset") {
            if(readCookie("randClicks")%3==0) {
                click = "field";
            } else if(readCookie("randClicks")%3==1) {
                click = "action";
            } else if(readCookie("randClicks")%3==2) {
                click = "action0";
            }
        } else {
            click = "fieldFull";
        }
        setTimeout("clickRandom('"+ click +"')", 100);
    }
}
function askRestart() {
    if (confirm("Restart?")) {
        window.location.href='./?action=restart';
    }
}
$(document).ready(function() {
    autostart();
    $("a").click(function() {
        removeOnclick();
    });
});