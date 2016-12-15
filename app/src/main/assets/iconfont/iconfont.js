;(function(window) {

  var svgSprite = '<svg>' +
    '' +
    '<symbol id="icon-1" viewBox="0 0 1024 1024">' +
    '' +
    '<path d="M510.005065 49.913806c-252.797563 0-458.441177 205.643614-458.441177 458.441177s205.643614 458.441177 458.441177 458.441177 458.441177-205.643614 458.441177-458.441177S762.802628 49.913806 510.005065 49.913806zM510.005065 909.194254c-221.034139 0-400.839271-179.846064-400.839271-400.839271 0-221.034139 179.805132-400.839271 400.839271-400.839271 220.993207 0 400.839271 179.805132 400.839271 400.839271C910.844336 729.34819 730.998271 909.194254 510.005065 909.194254z"  ></path>' +
    '' +
    '<path d="M675.869696 779.89133 349.602841 779.89133l0-62.093197 127.493719 0L477.096561 285.717324l-130.800022 37.84391L346.296539 257.425977l202.446805-58.786894 0 519.15905 127.125329 0L675.868673 779.89133z"  ></path>' +
    '' +
    '</symbol>' +
    '' +
    '<symbol id="icon-2" viewBox="0 0 1024 1024">' +
    '' +
    '<path d="M479.744 64c-229.888 0-416.256 186.368-416.256 416.256s186.368 416.256 416.256 416.256S896 709.632 896 479.744s-186.368-415.744-416.256-415.744z m0 768c-194.56 0-352.256-157.696-352.256-352.256s157.696-352.256 352.256-352.256 352.256 157.696 352.256 352.256-157.696 352.256-352.256 352.256z" fill="" ></path>' +
    '' +
    '<path d="M555.52 342.016c0-28.672-9.216-51.2-27.136-67.584-17.92-16.384-43.008-24.576-73.728-24.576-22.016 0-44.032 5.632-66.56 16.896-22.528 11.264-43.52 27.136-62.976 48.128v-65.536c17.408-17.92 37.376-31.744 59.392-41.472 22.016-9.728 48.128-14.336 77.824-14.336 48.128 0 87.04 12.8 116.224 38.912s44.032 60.928 44.032 104.96c0 39.424-9.216 73.216-27.136 101.888-17.92 28.672-48.64 57.856-92.16 88.064-44.544 30.72-74.24 52.736-89.088 66.048-14.848 13.312-25.6 26.112-31.232 38.4-6.144 12.288-8.704 27.136-8.704 44.544h264.704v59.904h-334.848v-26.624c0-30.72 4.096-56.832 12.8-78.336 8.192-21.504 22.016-42.496 41.472-62.464 19.456-20.48 49.152-44.544 90.112-73.216 41.472-29.184 70.144-55.296 85.504-77.312 13.824-23.04 21.504-48.128 21.504-76.288z" fill="" ></path>' +
    '' +
    '</symbol>' +
    '' +
    '</svg>'
  var script = function() {
    var scripts = document.getElementsByTagName('script')
    return scripts[scripts.length - 1]
  }()
  var shouldInjectCss = script.getAttribute("data-injectcss")

  /**
   * document ready
   */
  var ready = function(fn) {
    if (document.addEventListener) {
      if (~["complete", "loaded", "interactive"].indexOf(document.readyState)) {
        setTimeout(fn, 0)
      } else {
        var loadFn = function() {
          document.removeEventListener("DOMContentLoaded", loadFn, false)
          fn()
        }
        document.addEventListener("DOMContentLoaded", loadFn, false)
      }
    } else if (document.attachEvent) {
      IEContentLoaded(window, fn)
    }

    function IEContentLoaded(w, fn) {
      var d = w.document,
        done = false,
        // only fire once
        init = function() {
          if (!done) {
            done = true
            fn()
          }
        }
        // polling for no errors
      var polling = function() {
        try {
          // throws errors until after ondocumentready
          d.documentElement.doScroll('left')
        } catch (e) {
          setTimeout(polling, 50)
          return
        }
        // no errors, fire

        init()
      };

      polling()
        // trying to always fire before onload
      d.onreadystatechange = function() {
        if (d.readyState == 'complete') {
          d.onreadystatechange = null
          init()
        }
      }
    }
  }

  /**
   * Insert el before target
   *
   * @param {Element} el
   * @param {Element} target
   */

  var before = function(el, target) {
    target.parentNode.insertBefore(el, target)
  }

  /**
   * Prepend el to target
   *
   * @param {Element} el
   * @param {Element} target
   */

  var prepend = function(el, target) {
    if (target.firstChild) {
      before(el, target.firstChild)
    } else {
      target.appendChild(el)
    }
  }

  function appendSvg() {
    var div, svg

    div = document.createElement('div')
    div.innerHTML = svgSprite
    svgSprite = null
    svg = div.getElementsByTagName('svg')[0]
    if (svg) {
      svg.setAttribute('aria-hidden', 'true')
      svg.style.position = 'absolute'
      svg.style.width = 0
      svg.style.height = 0
      svg.style.overflow = 'hidden'
      prepend(svg, document.body)
    }
  }

  if (shouldInjectCss && !window.__iconfont__svg__cssinject__) {
    window.__iconfont__svg__cssinject__ = true
    try {
      document.write("<style>.svgfont {display: inline-block;width: 1em;height: 1em;fill: currentColor;vertical-align: -0.1em;font-size:16px;}</style>");
    } catch (e) {
      console && console.log(e)
    }
  }

  ready(appendSvg)


})(window)