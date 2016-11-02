dlzklib.Quickfilter = zk.$extends(zul.inp.InputWidget, {
    _label: '',
    _quickFilterButton: null,
    _quickFilterButtonClass: null,
    _quickFilterPopupSize: 0,
    $define: {
        label: function (val) {
             if ((this.desktop) && (this.$n("text")))
                this.$n("text").innerHTML = val;
        },

        quickFilterButton: function (val) {
            if (this.desktop)
                this.rerender();
        },
        
        quickFilterButtonClass: function (val) {
            if (this.desktop)
                this.rerender();
        },
        quickFilterPopupSize: function(val) {
            if ( this.desktop )
                this.rerender()
        }
    },

    bind_: function () {
        this.$supers(dlzklib.Quickfilter,'bind_', arguments);

        this.domListen_(this.getInputNode(), "onKeyUp", "_hideShowDelButton");
        this.domListen_(this.$n("del"), "onClick", "_doClear");
        this.domListen_(this.$n("magnifier"), "onClick", "_doSearch");
        this.domListen_(this.$n("list"), "onClick", "_doList");
        this._hideShowDelButton();
    },
    unbind_: function () {
        this.domUnlisten_(this.getInputNode(), "onKeyUp", "_hideShowDelButton");
        this.domUnlisten_(this.$n("del"), "onClick", "_doClear");
        this.domUnlisten_(this.$n("magnifier"), "onClick", "_doSearch");
        this.domUnlisten_(this.$n("list"), "onClick", "_doList");

        this.$supers(dlzklib.Quickfilter,'unbind_', arguments);
    },
    _doClear: function (evt) {
        this.getInputNode().value = '';
        this.updateChange_();
        this._hideShowDelButton();
        this.getInputNode().focus();
        this.fire("onChanging", this.getValue(), {});
    },
    _doSearch: function (evt) {
        this.updateChange_(); // ensure actual value is set
        this.fire("onOK", this.getValue(), {});
    },
    _doList: function (evt) {
        this.fire("onOpenPopup", this.getValue(), {});
    },
    _hideShowDelButton: function() {
        if (this.getInputNode().value)
            jq(this.$n("del")).css("display", "inline-block");
        else
            jq(this.$n("del")).css("display", "none");
    }

});
