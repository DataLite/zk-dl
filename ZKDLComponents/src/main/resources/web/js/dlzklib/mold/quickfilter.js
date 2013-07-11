function (out) {
    var zcls = this.getZclass(), uuid = this.uuid;

//    <div class="z-quickfilter">
//        <span class="z-quickfilter-text">Všechno možné i nemožné:</span>
//        <span class="z-quickfilter-list"></span>
//        <input class="z-quickfilter-real" type="text" value="" />
//        <span class="z-quickfilter-del"></span>
//        <span class="z-quickfilter-loupe"></span>
//    </div>
    out.push('<div', this.domAttrs_(), '>');
    out.push('<span id="', uuid, '-text" class="', zcls, '-text', '">', this.getLabel(), '</span>');
    out.push('<span id="', uuid, '-list" class="', zcls, '-list', '"></span>');
    out.push('<input id="', uuid, '-real" class="', zcls, '-real" type="text" value="',this.getValue(),'"/>');
    out.push('<span id="', uuid, '-del" class="', zcls, '-del', '"></span>');
    out.push('<span id="', uuid, '-magnifier" class="', zcls, '-magnifier', '"></span>');
    out.push('<br class="z-dlzklib-clear"/>');
    out.push('</div>');
}