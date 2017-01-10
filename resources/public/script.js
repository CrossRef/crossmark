(function() {

  var parseQueryString = function(query) {
    var data = {};
    var parts = query.substr(1).split('&');
    parts.forEach(function(part) {
      var values = part.split('=');
      data[decodeURIComponent(values[0])] = decodeURIComponent(values[1] || '');
    });
    return data;
  };

  var queryData = parseQueryString(window.location.search);
  
  // If the header is included in the templated HTML, adjust for vertical offset.
  // Included for content prior to 2.0 (header shown in widget container prior to this).
  // Not included for PDF container.
  var showHeader = (document.querySelector('.header') != null);

  if (showHeader) {
    var page = document.querySelector('.page');
    page.classList.add('page--with-header');
  }

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

  var collapseButtons = [].slice.call(document.querySelectorAll('[data-toggle]'), 0);

  collapseButtons.map(function(button) {
    var content = document.getElementById(button.getAttribute('data-toggle'));
    button.classList.add('is-collapsed');
    content.classList.add('is-collapsed');

    tapEvent(button, function(event) {

      button.classList.toggle('is-collapsed');
      content.classList.toggle('is-collapsed');

      event.preventDefault();
      event.stopPropagation();
    });
  });

})();
