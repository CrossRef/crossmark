/* Crossmark Widget
Version {{consts.version}}.
DO NOT RE-HOST THIS FILE.
*/
document.CROSSMARK = {
    VERIFICATION: "{{jwt}}",
    ENDPOINT: "{{consts.crossmark-server}}/dialog",
    SCRIPT_VERSION: "{{consts.version}}",
    STYLESHEET_URL: "{{consts.cdn-url}}/widget/v2.0/style.css",
    LOGO_URL: "{{consts.cdn-url}}/images/logo-crossmark.svg"
};

// Retrieve all meta tags.
document.CROSSMARK.getDoiMetaTags = function() {
  var tags = [];

  var metaTags = document.querySelectorAll("meta");
  for (var i in metaTags) {
    if (metaTags.hasOwnProperty(i)) {
      var tag = metaTags[i];

      // Only interested in dc.identifier .
      // We see DC.Identifier and DC.Identifier.DOI
      if ((tag.name || "").toLowerCase().match(/^dc\.identifier/)) {
        // If there is no scheme, accept. If there is a scheme, and it's not DOI, ignore.
        var scheme = (tag.getAttribute("scheme") || "").toLowerCase();
        var isDoi = tag.getAttribute('content') && tag.getAttribute('content').match(/(10\.\d+\/.*$)/);

        // We have some non-DOI tags.
        if (isDoi && (scheme === "" || scheme === "doi")) {
          tags.push(tag);
        }
      }
    }
  }

  return tags;
};

// Retrieve the DOI, or null.
document.CROSSMARK.getDoi = function() {
  var doiMeta = document.CROSSMARK.getDoiMetaTags()[0];
  if (doiMeta.length === 0) {
    return null;
  }

  var doi = doiMeta ? doiMeta.getAttribute('content').replace(/^(info:doi\/|doi:)/, '') : null;

  // Useful if we have a URL.
  var match = doi.match(/(10\.\d+\/.*$)/);
  if (match !== null) {
    doi = match[0];
  }

  return doi;
};

document.CROSSMARK.buildQueryString = function(data) {
  var query = [];
  for (var key in data) {
    if (data.hasOwnProperty(key)) {
      query.push(encodeURIComponent(key) + '=' + encodeURIComponent(data[key]));
    }
  }
  return '?' + query.join('&');
};

document.CROSSMARK.touchStarted = false;
document.CROSSMARK.touchArea = null;
document.CROSSMARK.tapEvent = function(element, callback) {
  element.addEventListener('click', function(event) {
    if (event.ctrlKey || event.shiftKey || event.metaKey || event.which !== 1) {
      return;
    }
    return callback(event);
  }, false);

  element.addEventListener('touchstart', function(event) {
    if (event.touches.length > 1) {
      document.CROSSMARK.touchStarted = false;
      return;
    }

    document.CROSSMARK.touchArea = { x: event.touches[0].screenX, y: event.touches[0].screenY };
    document.CROSSMARK.touchStarted = element;

    event.stopPropagation();
  }, false);

  window.addEventListener('touchstart', function(event) {
    document.CROSSMARK.touchStarted = false;
  });

  element.addEventListener('touchmove', function(event) {
    if (event.touches.length > 1) {
      document.CROSSMARK.touchStarted = false;
      return;
    }

    var newTouchArea = { x: event.touches[0].screenX, y: event.touches[0].screenY };
    if (Math.pow(document.CROSSMARK.touchArea.x - newTouchArea.x, 2) + Math.pow(document.CROSSMARK.touchArea.y - newTouchArea.y, 2) > 500) {
      document.CROSSMARK.touchStarted = false;
    }
  }, false);

  element.addEventListener('touchend', function(event) {
    if (document.CROSSMARK.touchStarted) {
      // var element = document.CROSSMARK.touchStarted;
      document.CROSSMARK.touchStarted = false;

      // var x = event.changedTouches[0].pageX - window.pageXOffset;
      // var y = event.changedTouches[0].pageY - window.pageYOffset;
      // var target = document.elementFromPoint(x, y);

      return callback(event);
    } else {
      event.preventDefault();
    }
  }, false);
};

// If there's already a dialog in the DOM but hiding, remove it.
document.CROSSMARK.erase = function() {
  var overlay = document.querySelector('.crossmark-overlay');
  if (overlay !== null && overlay.parentNode) {
    overlay.parentNode.removeChild(overlay);
  }
};

document.CROSSMARK.show = function() {
  document.CROSSMARK.erase();

  var isIos = /iPad|iPhone|iPod/.test(navigator.userAgent) && !window.MSStream;

  var doi = document.CROSSMARK.getDoi();

  var queryData = {
    doi: doi,
    domain: window.location.hostname,
    uri_scheme: window.location.protocol,
    cm_version: document.CROSSMARK.SCRIPT_VERSION,
    verification: document.CROSSMARK.VERIFICATION
  };

  var css = document.createElement('link');
  css.setAttribute('href', document.CROSSMARK.STYLESHEET_URL);
  css.setAttribute('type', 'text/css');
  css.setAttribute('rel', 'stylesheet');
  document.querySelector('head').appendChild(css);

  var widget = document.createElement('div');
  widget.setAttribute('id', 'crossmark-widget');
  
  // Initial display none to avoid a flash on load.
  widget.style.display = 'none';

  widget.innerHTML =
    '<div class="crossmark-reset crossmark-overlay"></div>' +
    '<div class="crossmark-reset crossmark-popup">' +
      '<div class="crossmark-reset crossmark-popup__offset">' +
        '<div class="crossmark-reset crossmark-popup__inner">' +
          '<div class="crossmark-reset crossmark-popup__header">' +
            '<a target="_blank" href="http://www.crossref.org/crossmark">' +
              '<img class="crossmark-reset crossmark-popup__logo">' +
            '</a>' +
            '<button class="crossmark-reset crossmark-popup__btn-close"></button>' +
          '</div>' +
          '<div class="crossmark-reset crossmark-popup__content-wrapper">' +
            '<iframe class="crossmark-reset crossmark-popup__content"></iframe>' +
          '</div>' +
        '</div>' +
      '</div>' +
    '</div>';

  var overlay = widget.querySelector('.crossmark-overlay');
  var popup = widget.querySelector('.crossmark-popup');
  var popupOffset = widget.querySelector('.crossmark-popup__offset');
  var popupInner = widget.querySelector('.crossmark-popup__inner');
  var logo = widget.querySelector('.crossmark-popup__logo');
  var content = widget.querySelector('.crossmark-popup__content');
  var closeButton = widget.querySelector('.crossmark-popup__btn-close');

  content.setAttribute('src', document.CROSSMARK.ENDPOINT + document.CROSSMARK.buildQueryString(queryData));

  if (isIos) {
    popupOffset.classList.add('is-ios');
  }

  logo.setAttribute('src', document.CROSSMARK.LOGO_URL);

  document.body.appendChild(widget);

  [overlay, popup, closeButton].map(function(element) {
    document.CROSSMARK.tapEvent(element, function(event) {
      widget.style.display = 'none';

      event.preventDefault();
      event.stopPropagation();
    });
  });

  document.CROSSMARK.tapEvent(popupInner, function(event) {
    event.stopPropagation();
  });

  widget.style.display = 'block';
  if (isIos) popupInner.style.top = window.scrollY + 'px';
};

// When loaded in a normal static document, register listeners.
// If a document is loaded without such links, e.g. an SPA, nothing is therefore registered.
document.addEventListener('DOMContentLoaded', function(event) {
  var links = [].slice.call(document.querySelectorAll('[data-target=crossmark]'), 0);
  links.map(function(link) {
    link.style.cursor = 'pointer';
    document.CROSSMARK.tapEvent(link, function(event) {
      document.CROSSMARK.show();
      
      event.preventDefault();
      event.stopPropagation();
    });
  });
});
