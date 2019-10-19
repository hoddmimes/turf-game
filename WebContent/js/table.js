
/*
    Below a set of table auxillary routines are defined.
    They all assume that a table definition object is provided when invoking the routines
    The table defion object has the following layout
    {'table' :'<table-name>',
     'keys' : [ {'title' : '<colum-header>', 'column' : <table-column>', jkey' : '<object-key-name-within-a-jobject>' }, ... ]
    }
*/


function tableClear( pTable, pTabDef ) {
   var tSize = pTable.rows.length;
   for (var i = tSize - 1; i > 0; i--) {
     pTable.deleteRow(i);
   }
}


function tabGetJKey( pTabDef, jKey )
{
    var tKeys = pTabDef.keys;
    for( var i = 0; i <  tKeys.length; i++ ) {
      var k = tKeys[i];
      if (k.jkey == jKey) {
            return k;
      }
    }
    return null;
}

function tabFindRow( pTable, pTabKey, jKeyValue ) {

    for( var i = 1; i < pTable.rows.length; i++) {
      if (pTable.rows[i].cells[ pTabKey.column].innerHTML == jKeyValue) {
        return pTable.rows[i];
      }
    }
    return null;
}

function tableDeleteRow( pTable, pTabDef, jKey, jKeyValue ) {
    var tTabKey = tabGetJKey( jKey );
    if (tTabKey == null) {
        return Boolean( false );
    }

    for( var i = 1; i < pTable.rows.length; i++) {
      if (pTable.rows[i].cells[ tTabKey.column].innerHTML == jKeyValue) {
         pTable.deleteRow(i);
         return Boolean( true );
      }
    }

    return Boolean( false );
}

function updateTable( pTable, pTabDef, jKey, jKeyValue, jObject)
{
  var tTabKey = tabGetJKey( jKey );
  if (tTabKey == null) {
    return Boolean( false );
  }

  var tRow = tabFindRow( pTable, tTabKey, jKeyValue );
  if (tRow == null) {
    return Boolean( false );
  }

  var tKeys = pTabDef.keys;
  for( var i = 0; i <  tKeys.length; i++ ) {
      var k = tKeys[i];
      var v = jObject[k.jkey];
      if (v != null) {
           tRow.cells[ k.column ].innerHTML = v;
      }
  }
  return Boolean( true );
}

function tableInsert( pTable, pTabDef, jObject )
{
   var tRow = pTable.insertRow( pTable.rows.length)
   var tKeys = pTabDef.keys;

   for( var i = 0; i <  tKeys.length; i++ ) {
      tRow.insertCell(i);
      if (tKeys[i].cssClass != null) {
        tRow.cells[ i ].innerHTML = "";
        tRow.cells[ i ].className = tKeys[i].cssClass;
      }
    }

    if (pTabDef.onClick != 0)
    {
      var fn = window[pTabDef.onClick];
      tRow.onmouseup = function() {fn.apply(null, [tRow]);}
    }

   for( var i = 0; i <  tKeys.length; i++ ) {
       var k = tKeys[i];
       var v = jObject[k.jkey];
       if (v != null) {
         tRow.cells[ k.column ].innerHTML = v;
       }
       if (k.onClick != null) {
          var fn = window[k.onClick];
          row.cells[ k.column ].onmouseup =  function() {fn.apply(null, [[k.jkey,tRow ]]);}
       }
   }
}

