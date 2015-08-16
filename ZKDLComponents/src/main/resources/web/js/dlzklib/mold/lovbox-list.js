(function (out) {
    var uuid = this.uuid,
        zcls = this.getZclass();

    out.push('<i', this.domAttrs_(), '>');

    // hide input box
    out.push('<input id="', uuid, '-real" style="display:none"/>');

    // draw popup content directly
    out.push('<div id="', this.uuid, '-list" class="', this.getZclass(), '-list" height="500px">');
    var popup = this.firstChild;
    for (var w = popup.firstChild; w; w = w.nextSibling)
        w.redraw(out);
    out.push('</div>');

    // empty popup

    out.push('</i>');
})