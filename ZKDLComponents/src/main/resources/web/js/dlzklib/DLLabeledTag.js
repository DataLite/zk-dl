dlzklib.DLLabeledTag = zk.$extends(zul.Widget, {
	_label: "",
	_value: "",

   $define: {
       label: function(label) {
		   if (this.desktop) {
			  this.$n("label").innerHTML = zUtl.encodeXML(label);
		   }
       },
       value: function(value) {
		   if (this.desktop) {
			   this.$n("value").innerHTML = zUtl.encodeXML(value);
		   }
       }
    },

    bind_: function () {
        this.$supers(dlzklib.DLLabeledTag, 'bind_', arguments);
        this.domListen_(this.$n("close"), "onClick", "doRemove");
    },

    unbind_: function () {
        this.domUnlisten_(this.$n("close"), "onClick", "doRemove");
        this.$supers(dlzklib.DLLabeledTag, 'unbind_', arguments);
    },

    doRemove: function(evt) {
        this.fire("onRemove", this.getValue(), {});
        var $el = jq(this);
        $el.hide('fast', function(){ $el.remove(); });
    }/*,

    initDrag_: function () {
        dlzklib.DLLabeledTag.initDrag_.apply(this, arguments); //if you want to call back the default implementation
    }*/

});