// Tento soubor prosim NEmergovat se starsimi verzemi, duvod: upraveny kod.
(function (out) {

    var uuid = this.uuid,
        zcls = this.getZclass();

    out.push('<i', this.domAttrs_(), '><input id="',
        uuid, '-real" style="display:none"/>');
    //out.push('<i>');
    //out.push('<i class="z-btn z-button">');

    if (this.getImageLabel()) {
        out.push('<div id="', uuid, '-label-btn" class="', zcls, '-label-btn z-button">');
        out.push('<span>',this.getImageLabel(),'</span>');

        out.push('<i id="', uuid, '-btn" class="z-icon-three-bars"></i>');
        out.push('</div>');

    } else {
        out.push('<i id="', uuid, '-btn" class="z-icon-three-bars"></i>');
    }


    out.push();

    this.redrawpp_(out);

    //out.push('</i>');
    out.push('</i>');
});