


function getUrlParam() {
    var vars = {};
    var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
        vars[key] = value;
    });
    return vars;
}




function validateEmail(email)
{
    var re = /[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/;
    return re.test(email);
}

function validateDate(pDate)
{
    var re = /^\d{4}-\d{1,2}-\d{1,2}$/;
    return re.test( pDate );
}

function loadUrl(newLocation)
{
  window.location = newLocation;
  return false;
}


function utf8Encode(s) {
  return unescape(encodeURIComponent(s));
}

function utf8Decode(s) {
  return decodeURIComponent(escape(s));
}

function utf8EncodeArray( pArray ) {
    for( var i = 0; i < pArray.length; i++) {
        pArray[i] = utf8Encode( pArray[i] );
    }
    return pArray;
}

function utf8DecodeArray( pArray ) {
    for( var i = 0; i < pArray.length; i++) {
        pArray[i] = utf8Decode( pArray[i] );
    }
    return pArray;
}

function isEmpty(x)
{
   return (
        (typeof x == 'undefined')
                    ||
        (x == null)
                    ||
        (x == false)  //same as: !x
                    ||
        (x.length == 0)
                    ||
        (x == "")
                    ||
        (x.replace(/\s/g,"") == "")
                    ||
        (!/[^\s]/.test(x))
                    ||
        (/^\s*$/.test(x))
  );
}

function isNumeric( pStr ) {
	var isNumericTxt = /^[-+]?(\d+|\d+\.\d*|\d*\.\d+)$/;
	return isNumericTxt.test( pStr );
}
function formatErrorMessage(jqXHR, exception) {

    if (jqXHR.status === 0) {
        return ('Not connected.\nPlease verify your network connection.');
    } else if (jqXHR.status == 404) {
        return ('The requested page not found. [404]');
    } else if (jqXHR.status == 500) {
        return ('Internal Server Error [500].');
    } else if (exception === 'parsererror') {
        return ('Requested JSON parse failed.');
    } else if (exception === 'timeout') {
        return ('Time out error.');
    } else if (exception === 'abort') {
        return ('Ajax request aborted.');
    } else {
        return ('Uncaught Error.\n' + jqXHR.responseText);
    }
}

function turfError(xhr, err) {
    var responseTitle= $(xhr.responseText).filter('title').get(0);
    alert($(responseTitle).text() + "\n" + formatErrorMessage(xhr, err) );
}
