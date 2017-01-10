/* Crossmark Widget
Version {{crossmark-version}}.
DO NOT RE-HOST THIS FILE.
*/
document.addEventListener('DOMContentLoaded', function(event) {
  var SETTINGS = {
    VERIFICATION: "{{jwt}}",
    ENDPOINT: '{{consts.crossmark-server}}',
    SCRIPT_VERSION: '{{consts.version}}',
    STYLESHEET_URL: '{{consts.cdn-url}}/widget/v2.0/style.css',
  };

  var touchStarted = false;
  var touchArea;
  var tapEvent = function(element, callback) {
    element.addEventListener('click', function(event) {
      if (event.ctrlKey || event.shiftKey || event.metaKey || event.which !== 1) {
        return;
      }
      return callback(event);
    }, false);

    element.addEventListener('touchstart', function(event) {
      if (event.touches.length > 1) return touchStarted = false;
      touchArea = { x: event.touches[0].screenX, y: event.touches[0].screenY };
      touchStarted = element;

      event.stopPropagation();
    }, false);

    window.addEventListener('touchstart', function(event) {
      touchStarted = false;
    });

    element.addEventListener('touchmove', function(event) {
      if (event.touches.length > 1) return touchStarted = false;
      var newTouchArea = { x: event.touches[0].screenX, y: event.touches[0].screenY };
      if (Math.pow(touchArea.x - newTouchArea.x, 2) + Math.pow(touchArea.y - newTouchArea.y, 2) > 500) {
        touchStarted = false;
      }
    }, false);

    element.addEventListener('touchend', function(event) {
      if (touchStarted) {
        var element = touchStarted;
        touchStarted = false;

        var x = event.changedTouches[0].pageX - window.pageXOffset;
        var y = event.changedTouches[0].pageY - window.pageYOffset;
        var target = document.elementFromPoint(x, y);

        return callback(event);
      } else {
        event.preventDefault();
      }
    }, false);
  };

  var buildQueryString = function(data) {
    var query = [];
    for (var key in data) {
      query.push(encodeURIComponent(key) + '=' + encodeURIComponent(data[key]));
    }
    return '?' + query.join('&');
  };

  var isIos = /iPad|iPhone|iPod/.test(navigator.userAgent) && !window.MSStream;

  // Scan for case-insensitive.
  var getDoiMeta = function() {
    var metaTags = document.querySelectorAll("meta");
    for (var i in metaTags) {
      if (metaTags.hasOwnProperty(i)) {
        var tag = metaTags[i];
        if ((tag.name || "").toLowerCase() == "dc.identifier") {
          return tag;
        }
      }
    }
  }

  var doiMeta = getDoiMeta();
  var doi = doiMeta ? doiMeta.getAttribute('content').replace(/^(info:doi\/|doi:)/, '') : null;

  var queryData = {
    doi: doi,
    domain: window.location.hostname,
    uri_scheme: window.location.protocol,
    cm_version: SETTINGS.SCRIPT_VERSION
  };

  var css = document.createElement('link');
  css.setAttribute('href', SETTINGS.STYLESHEET_URL);
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
  var content = widget.querySelector('.crossmark-popup__content')
  var closeButton = widget.querySelector('.crossmark-popup__btn-close');

  if (isIos) popupOffset.classList.add('is-ios');
  // logo.setAttribute('src', SETTINGS.LOGO_URL);

  document.body.appendChild(widget);

  [overlay, popup, closeButton].map(function(element) {
    tapEvent(element, function(event) {
      widget.style.display = 'none';

      event.preventDefault();
      event.stopPropagation();
    });
  });

  tapEvent(popupInner, function(event) {
    event.stopPropagation();
  });

  var initialised = false;

  // delete queryData.domain;

  var links = [].slice.call(document.querySelectorAll('[data-target=crossmark]'), 0);
  links.map(function(link) {
    link.style.cursor = 'pointer';
    link.setAttribute('href', SETTINGS.ENDPOINT + buildQueryString(queryData));

    tapEvent(link, function(event) {
      if (!initialised) {
        content.setAttribute('src', SETTINGS.ENDPOINT + buildQueryString(queryData));
        initialised = true;
      }
      widget.style.display = 'block';
      if (isIos) popupInner.style.top = window.scrollY + 'px';

      event.preventDefault();
      event.stopPropagation();
    });
  });
});
