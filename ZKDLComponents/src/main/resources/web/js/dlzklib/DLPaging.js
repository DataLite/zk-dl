dlzklib.DLPaging = zk.$extends(zul.mesh.Paging, {
    _infoText: '', // infotext is rendered on server
    _knownPageCount: true, // render ? instead of pageCount number

    $define: {
        knownPageCount: _zkf = function () {
            this.rerender();
        },
        infoText: _zkf
    },

    bind_: function () {
        this.$supers(dlzklib.DLPaging,'bind_', arguments);

        var uuid = this.uuid,
            zcls = this.getZclass();

        // unknown total
        if (!this.getKnownPageCount())
            jq(jq.$$(uuid, 'last')[0]).addClass(zcls + "-btn-disd");
    },
    unbind_: function () {
        this.$supers(dlzklib.DLPaging,'unbind_', arguments);
    },

    infoText_: function () {
        return this.getInfoText();
    },

    redrawChildren: function(out) {
        for (var w = this.firstChild; w; w = w.nextSibling)
            w.redraw(out);
    },

    _updatePageNum: function () {
        // calculated from server only
    },

    getZclass: function () {
        return "z-paging";
    }
},{  // static methods
    _increase: function (inp, wgt, add){
        var value = zk.parseInt(inp.value);
        value += add;
        if (value < 1) value = 1;
        else if (wgt._knownPageCount && value > wgt._pageCount) value = wgt._pageCount;
        inp.value = value;
    }
});
