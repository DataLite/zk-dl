dlzklib.Lovbox = zk.$extends(zul.inp.Bandbox, {
    _clearButton: true,

    $define: {
        imageClass: function (v) { // image mold
            var n = this.getImageNode();
            if (n) n.class = v || '';
        },

        clearButton: function () {
            this.redraw_();
        }

        //-disd

    },

    bind_: function () {
        this.$supers(dlzklib.Lovbox, 'bind_', arguments);
        this.domListen_(this.$n("del"), "onClick", "_doClear");
    },

    unbind_: function () {
        this.domUnlisten_(this.$n("del"), "onClick", "_doClear");
        this.$supers(dlzklib.Lovbox, 'unbind_', arguments);
    },

    _doClear: function (evt) {
        this.fire("onClear", null, {});
        evt.stop();
    },

    doMouseOut_: function () {
        if (!this.isOpen())
            this.visibleDelNode(false);
        this.$supers('doMouseOut_', arguments);
    },
    doMouseOver_: function () {
        this.visibleDelNode(true);
        this.$supers('doMouseOver_', arguments);
    },

    close: function (opts) {
        this.visibleDelNode(false);
        this.$supers('close', arguments);
    },

    inImageMold: function () {
        return this.getMold().indexOf("image") != -1;
    },

    getImageNode: function () {
        return this.$n("");
    },

    visibleDelNode: function ( visible ) {
        var del = this.$n("del");
        if (del) {
            if (visible && this._clearButton && this.getText() )
                del.style.display = "inline"
            else
                del.style.display = "none"
        }

    },

    // add class z-lovbox
    getSclass: function () {
        var sclass = this.$supers('getSclass', arguments);
        return 'z-lovbox' + (sclass ? ' ' + sclass : '');
    },

    // from Bandbox, add class z-lovbox-pp
    redrawpp_: function (out) {
        out.push('<div id="', this.uuid, '-pp" class="z-lovbox-pp ', this.getZclass(),
            '-pp" style="display:none" tabindex="-1">');

        for (var w = this.firstChild; w; w = w.nextSibling)
            w.redraw(out);

        out.push('</div>');
    },

    // from Combobox, add delete button span
    redraw_: _zkf = function (out) {
        var uuid = this.uuid,
            zcls = this.getZclass(),
            isButtonVisible = this._buttonVisible;

        out.push('<i', this.domAttrs_({text:true}), '><input id="',
            uuid, '-real" class="', zcls, '-inp');

        if(!isButtonVisible)
            out.push(' ', zcls, '-right-edge');

        out.push('" autocomplete="off"',this.textAttrs_(), '/>');

        out.push('<i id="', uuid, '-btn" class="',
            zcls, '-btn');

        if (this.inRoundedMold()) {
            if (!isButtonVisible)
                out.push(' ', zcls, '-btn-right-edge');
            if (this._readonly)
                out.push(' ', zcls, '-btn-readonly');
            if (zk.ie6_ && !isButtonVisible && this._readonly)
                out.push(' ', zcls, '-btn-right-edge-readonly');
        } else if (!isButtonVisible)
            out.push('" style="display:none');

        out.push('">');
        out.push('<div class="', zcls, '-btn-icon"></div></i>');

        out.push('<span id="', uuid, '-del" class="', zcls, '-del', '"/>');

        this.redrawpp_(out);

        out.push('</i>');
    },

    // Bandbox component overrides onOk - we need it for quick filter
    enterPressed_ : function () {}
}, {
    $redraw: _zkf
})