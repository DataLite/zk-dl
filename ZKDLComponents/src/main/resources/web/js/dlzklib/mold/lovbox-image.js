(function (out) {
    var uuid = this.uuid,
        zcls = this.getZclass();

    out.push('<i', this.domAttrs_(), '><input id="',
        uuid, '-real" style="display:none"/>');

    // noinspection JSUnresolvedFunction
    if (this.getImageLabel()) {
        var disabled="" ;
        // noinspection JSUnresolvedFunction
        if ( ( this.getLovboxReadonly() ) || ( this.getDisabled() ) ) {
            disabled="-disd" ;
        }
        out.push('<div id="', uuid, '-label-btn" class="', zcls, '-label-btn z-button-dtl', disabled, '">');
        // noinspection JSUnresolvedFunction
        out.push('<span class="fl">',this.getImageLabel(),'</span>');

        out.push('<i id="', uuid, '-btn" class="', zcls, '-image-btn"></i>');
        out.push('</div>');

    } else {
        out.push('<i id="', uuid, '-btn" class="', zcls, '-image-btn"></i>');
    }


    out.push();

    this.redrawpp_(out);

    out.push('</i>');
}) ;