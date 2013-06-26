function (out) {
    var uuid = this.uuid,
        zcls = this.getZclass();

    out.push('<i', this.domAttrs_(), '><input id="',
        uuid, '-real" style="display:none"/>');

    out.push('<i id="', uuid, '-btn" class="', zcls, '-image-btn');

    out.push('"></i>');

    this.redrawpp_(out);

    out.push('</i>');
}