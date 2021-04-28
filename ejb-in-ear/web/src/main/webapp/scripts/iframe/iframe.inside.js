/**
 * This function update tabs content. this function is called inside IFrame
 *
 */

function iResizeInsideIFrame() {
    // Set specific variable to represent all iframe tags.
    var iFrames = parent.document.getElementsByTagName('iframe');

    // Iterate through all iframes in the page.
    for (var i = 0, j = iFrames.length; i < j; i++) {
        // Set inline style to equal the body height of the iframed content.
        iFrames[i].style.height = iFrames[i].contentWindow.document.body.offsetHeight + 'px';
    }
}