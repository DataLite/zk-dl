dlzklib.Lovbox = zk.$extends(zul.inp.Bandbox, {
    _clearButton: false,
    _lovboxReadonly: false,
    _watermark: '',
    _defaultColor: '',
    _watermarkColor: '#aaa',
    _imageLabel: '',

    $define: {
        clearButton: function () {
            if (this.desktop)
                this.rerender();
        },

        watermark: function (v) {
            if (this.desktop) {
                $(this.getInputNode()).Watermark(v);
            }
        },

        lovboxReadonly: function() {
            if (this.desktop)
                this.rerender();
        },

        imageLabel: function() {
            if (this.desktop)
                this.rerender();
        }

        //-disd

    },

    bind_: function () {
        this.$supers(dlzklib.Lovbox, 'bind_', arguments);
        var labelBtn = this.$n("label-btn");
        if (labelBtn) {
            this.domListen_(labelBtn, "onClick", "_clickOpen");
        }


        if (!this.getLovboxReadonly()) {
            this.domListen_(this.$n("del"), "onClick", "_doClear");
            this.domListen_(this.getInputNode(), "onFocus", "_clearWatermark");
            this.domListen_(this.getInputNode(), "onBlur", "_doWatermark");
            this.domListen_(this.getInputNode(), "onChange", "_doWatermark");
        }

        this._doWatermark();
    },

    unbind_: function () {
        var labelBtn = this.$n("label-btn");
        if (labelBtn) {
            this.domUnlisten_(labelBtn, "onClick", "_clickOpen");
        }

        if (!this.getLovboxReadonly()) {
            this.domUnlisten_(this.$n("del"), "onClick", "_doClear");
            this.domUnlisten_(this.getInputNode(), "onFocus", "_clearWatermark");
            this.domUnlisten_(this.getInputNode(), "onBlur", "_doWatermark");
            this.domUnlisten_(this.getInputNode(), "onChange", "_doWatermark");
        }

        this.$supers(dlzklib.Lovbox, 'unbind_', arguments);
    },

    // additional open to onClick
    _clickOpen: function (evt) {
        if (! this._readonly) {
            this.open({sendOnOpen: true});
        }
        evt.stop();
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
            if (visible && this.getClearButton() && this.getText() && !this.getLovboxReadonly() )
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

        out.push('<span id="', uuid, '-del" class="', zcls, '-del', '"></span>');

        this.redrawpp_(out);

        out.push('</i>');
    },

    // Bandbox component overrides onOk - we need it for quick filter
    enterPressed_ : function () {},

    _doWatermark: function () {
        var input = $(this.getInputNode()),
            watermark = this.getWatermark();

        if (!this._defaultColor)
            this._defaultColor = input.css("color");

        if(watermark && !this.getLovboxReadonly() && (input.val().length==0 || input.val()==watermark)) {
            input.val(watermark);
            input.css("color",this._watermarkColor);
        } else {
            input.css("color",this._defaultColor);
        }
    },

    _clearWatermark: function() {
        var input = $(this.getInputNode()),
            watermark = this.getWatermark();
        if(input.val()==watermark)
            input.val("");
        input.css("color",this._defaultColor);
    },

    setValue: function() {
        this.$supers('setValue', arguments);
        this._doWatermark();
    }
}, {
    $redraw: _zkf
})