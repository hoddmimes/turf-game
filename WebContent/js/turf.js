

const iconSize = 32;



  function createIcon( rgb ) {
  
      var tIconMarker = {
	  width: iconSize,
	  height: iconSize,
	  rgb : rgb,
	  data: new Uint8Array(iconSize * iconSize * 4),

	  // When the layer is added to the map,
	  // get the rendering context for the map canvas.
	  onAdd: function () {
	      const canvas = document.createElement('canvas');
	      canvas.width = this.width;
	      canvas.height = this.height;
	      this.context = canvas.getContext('2d');
	  },

	  // Call once before every frame where the icon will be used.
	  render: function () {

	      const markerWidth = 6;
	      
	      const hx_start = 0;                  // Horizontal X starting point
	      const hy_start = (iconSize / 2) - (markerWidth/2); // Horizontal Y starting point
	      const vx_start = (iconSize / 2) - (markerWidth/2); // Vertical X starting point
	      const vy_start = 0;                  // Vertical Y starting point


	
	      const context = this.context;

	      context.clearRect(0, 0, this.width, this.height);

	      context.beginPath();
	      context.rect(  hx_start, // x starting point
		        hy_start, // y starting point
		        iconSize,
		        markerWidth );
	      context.fillStyle = 'rgba(' + this.rgb.red + ',' + this.rgb.green + ',' + this.rgb.blue + ')';
	      context.fill();


	      context.beginPath();
 	      context.rect(  vx_start, // x starting point
		        vy_start, // y starting point
		        markerWidth,
		        iconSize );
	      context.fillStyle = 'rgba(' + this.rgb.red + ',' + this.rgb.green + ',' + this.rgb.blue + ')';
	      context.fill();



	      this.data = context.getImageData( 0,0,this.width,this.height).data;

	      map.triggerRepaint();

	      // Return `true` to let the map know that the image was updated.
	      return true;
	  }
      };
      return tIconMarker;
  };


  function createZoneIcon() {
  
      var tIconMarker = {
	  width: iconSize,
	  height: iconSize,
	  data: new Uint8Array(iconSize * iconSize * 4),

	  // When the layer is added to the map,
	  // get the rendering context for the map canvas.
	  onAdd: function () {
	      const canvas = document.createElement('canvas');
	      canvas.width = this.width;
	      canvas.height = this.height;
	      this.context = canvas.getContext('2d');
	  },

	  // Call once before every frame where the icon will be used.
	  render: function () {
	      const markerWidth = 6;
	      const markerOffset = 4;

	      const thx_start = markerOffset;                  // Horizontal X start
	      const thy_start = markerOffset;                  // Horizontal Y Start

	      
	      const bhx_start = markerOffset;                  // Horizontal X start
	      const bhy_start = iconSize - (markerWidth + markerOffset)// Horizontal Y Start

	      const lvx_start = markerOffset;                  // Vertical  Left X start
	      const lvy_start = markerOffset;                  // Vertical Left Y Start
	      

     	      const rvx_start = iconSize - (markerWidth + markerOffset); // Vertical  Right X start
	      const rvy_start = markerOffset;                  // Vertical Right Y Start
	      

	      const bar_height = iconSize - (2 * markerOffset); // Horizontal width
	      const bar_width =  iconSize - (2 * markerOffset); // Horizontal width
	      
	      
	      const context = this.context;

	      context.clearRect(0, 0, this.width, this.height);

	      // top bar
	      context.beginPath();
	      context.rect(thx_start, // x starting point
		           thy_start, // y starting point
		           bar_width, // width
		           markerWidth ); // height
	      context.fillStyle = "#FF0000";
	      context.fill();

	      // bottom bar
	      context.beginPath();
 	      context.rect( bhx_start,  // x starting point
		            bhy_start, // y starting point
		            bar_width,
		            markerWidth);
	      context.fillStyle = "#FF0000";
	      context.fill();


	      // left bar
	      context.beginPath();
	      context.rect(lvx_start, // x starting point
		           lvy_start, // y starting point
		           markerWidth,  // width
		           bar_height ); // height
	      context.fillStyle = "#FF0000";
	      context.fill();

	      // right bar
	      context.beginPath();
	      context.rect(rvx_start,
		           rvy_start, // y starting point
		           markerWidth,  // width
		           bar_height ); // height
	      context.fillStyle = "#FF0000";
	      context.fill();

	      

	      this.data = context.getImageData( 0,0,this.width,this.height).data;

	      map.triggerRepaint();

	      // Return `true` to let the map know that the image was updated.
	      return true;
	  }
      };
      return tIconMarker;
  };





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
