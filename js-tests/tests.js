var webpage = require('webpage');
var system = require('system');

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

  {name: "meta-dc-with-scheme",
   description: "Widget should extract DOI from 'dc.identifier' meta tag when the tag has a 'scheme' attribute of 'doi'.",
   file: "meta-dc-with-scheme.html",
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

  {name: "meta-dc-without-scheme",
   description: "Widget should extract DOI from 'dc.identifier' meta tag when the tag has no 'scheme' attribute.",
   file: "meta-dc-without-scheme.html",
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
  },

  {name: "meta-dc-case-insensitive",
   description: "Widget should extract DOI from 'dc.identifier' meta tag, tag name case insensitive.",
   file: "meta-dc-case-insensitive.html",
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

  {name: "meta-dc-conflict",
   description: "Widget should extract DOI when there are multiple meta tags including mulitple 'dc.identifier's but only one is a DOI.",
   file: "meta-dc-conflict.html",
   f: function(page) {
      var href = page.evaluate(function(){
        return document.getElementById("button").getAttribute("href");
      });

     // Should extract DOI and place it in the link, URL encoded.
     if (href.indexOf("10.5555%2F98754321") == -1) {
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

  // Run all tests unless a test name was supplied.
  if (system.args.length === 1 || system.args[1] === test.name) {
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

      // Recurse on result.
      runNext(tests, totalExecuted, totalFailed);
    }

    page.open(url, function(status) {
      console.log("Opened page: " + url + " status: "+ status);
    });
  } else {
    // If we didn't execute the test, move onto the next one.
    runNext(tests, totalExecuted, totalFailed);
  }
}


runNext(tests, 0, 0);
