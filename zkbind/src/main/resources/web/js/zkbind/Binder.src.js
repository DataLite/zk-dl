
(function () {
	var _WidgetX = {};

zk.override(zk.Widget.prototype, _WidgetX, {
	$binder: function () {
		var w = this;
		for (; w ; w = w.parent) {
			if (w['$ZKBINDER$'])
				break;
		}
		if (w) {
			if (!w._$binder)
				w._$binder = new zkbind.Binder(w, this); 
			return w._$binder;
		}
		return null;
	},
	$afterCommand: function (command, args) {
		var binder = this.$binder();
		if (binder)
			binder.$doAfterCommand(command, args);
	},
	unbind_: function () {
		if (this._$binder) {
			this._$binder.destroy();
			this._$binder = null;
		}
		_WidgetX.unbind_.apply(this, arguments);
	}
});

zkbind.$ = function (n, opts) {
	var widget = zk.Widget.$(n, opts);
	if (widget)
		return widget.$binder();
	zk.error('Not found ZK Binder with [' + n + ']');
};

zkbind.Binder = zk.$extends(zk.Object, {
	$init: function (widget, currentTarget) {
		this.$supers('$init', arguments);
		this.$view = widget;
		this.$currentTarget = currentTarget;
		this._aftercmd = {};
	},
	
	after: function (cmd, fn) {
		if (!fn && jq.isFunction(cmd)) {
			fn = cmd;
			cmd = this._lastcmd;
		}
			
		var ac = this._aftercmd[cmd];
		if (!ac) this._aftercmd[cmd] = [fn];
		else {
			ac.push(fn);
		}
		return this;
	},
	
	unAfter: function (cmd, fn) {
		var ac = this._aftercmd[cmd];
		for (var j = ac ? ac.length: 0; j--;) {
			if (ac[j] == fn)
				ac.splice(j, 1);
		}
		return this;
	},
	
	destroy: function () {
		this._aftercmd = null;
		this.$view = null;
		this.$currentTarget = null;
	},
	
	command: function (cmd, args) {
		this.$command0(cmd, args);
		return this;
	},
	
	$command0: function (cmd, args, timeout) {
		var wgt = this.$view;
		if (timeout) {
			setTimeout(function () {
				zAu.send(new zk.Event(wgt, "onBindCommand", {cmd: cmd, args: args}, {toServer:true}));
			}, timeout); 
		} else {
			zAu.send(new zk.Event(wgt, "onBindCommand", {cmd: cmd, args: args}, {toServer:true}));
		}
		this._lastcmd = cmd;
	},
	
	globalCommand: function (cmd, args) {
		this.$globalCommand0(cmd, args);
		return this;
	},
	
	$globalCommand0: function (cmd, args, timeout) {
		var wgt = this.$view;
		if (timeout) {
			setTimeout(function () {
				zAu.send(new zk.Event(wgt, "onBindGlobalCommand", {cmd: cmd, args: args}, {toServer:true}));
			}, timeout); 
		} else {
			zAu.send(new zk.Event(wgt, "onBindGlobalCommand", {cmd: cmd, args: args}, {toServer:true}));
		}
		this._lastcmd = cmd;
	},
	$doAfterCommand: function (cmd, args) {
		var ac = this._aftercmd[cmd];
		for (var i = 0, j = ac ? ac.length: 0; i < j; i++) {
			ac[i].apply(this, [args]);
		}
	}
}, {
	
	postCommand: function (dom, command, args) {
		var w = zk.Widget.$(dom);
		if (w) {
			var binder = w.$binder();
			if (binder) {
				binder.command(command, args);
			}
		}
	},
	
	postGlobalCommand: function (dom, command, args) {
		var w = zk.Widget.$(dom);
		if (w) {
			var binder = w.$binder();
			if (binder) {
				binder.globalCommand(command, args);
			}
		}
	}
});
})();