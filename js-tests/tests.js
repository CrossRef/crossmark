var webpage = require('webpage');
var base = "http://localhost:8000/test/";

var tests = [
  {name: "meta-dc-doi",
   description: "Widget should extract DOI from 'dc.identifier' meta tag with a 'doi:' prefix.",
   file: "meta-dc-doi.html",
   f: function(page) {
      var href = page.evaluate(function(){
        return document.getElementById("button").getAttribute("href");
      });

     // Should extract DOI and place it in the link, URL encoded.
     if (href.indexOf("10.5555%2F11111") == -1) {
       return false;
     }
     return true;
   }
  },

  {name: "meta-dc-http",
   description: "Widget should extract DOI from 'dc.identifier' meta tag with an HTTP URL.",
   file: "meta-dc-http.html",
   f: function(page) {
      var href = page.evaluate(function(){
        return document.getElementById("button").getAttribute("href");
      });

     // Should extract DOI and place it in the link, URL encoded.
     if (href.indexOf("10.5555%2F22222") == -1) {
       return false;
     }
     return true;
   }
  },

  {name: "meta-dc-plain",
   description: "Widget should extract DOI from 'dc.identifier' meta tag with plain un-prefixed DOI.",
   file: "meta-dc-plain.html",
   f: function(page) {
      var href = page.evaluate(function(){
        return document.getElementById("button").getAttribute("href");
      });

     // Should extract DOI and place it in the link, URL encoded.
     if (href.indexOf("10.5555%2F33333") == -1) {
       return false;
     }

     return true;
   }
  }

];


function runNext(tests, totalExecuted, totalFailed) {
  var test = tests.pop();

  // Got to the end, report.
  if (!test) {
    if (totalFailed === 0) {
      console.log("All " + totalExecuted + " tests passed!");
      phantom.exit(0);  
    } else {
      console.log("FAILURES: " + totalFailed + " failed out of " + totalExecuted);
      phantom.exit(1);
    }
  }

  console.log("Load: " + test.name);

  // Or continue.
  var page = webpage.create();
  var url = base + test.file;

  page.onConsoleMessage = function(msg, lineNum, sourceId) {
    console.log('CONSOLE: ' + msg + ' (from line #' + lineNum + ' in "' + sourceId + '")');
  };

  page.onCallback = function(data) {
    console.log("Start: " + test.name);
    var result = test.f(page);

    if (!result) {
      console.error("FAIL: " + test.name);
      totalFailed++;
    }

    totalExecuted++;

    // Recurse.
    runNext(tests, totalExecuted, totalFailed);
  };

  page.open(url, function(status) {
    console.log("Opened page: " + url + " status: "+ status);
  });
}


runNext(tests, 0, 0);
