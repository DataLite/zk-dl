
function (out) {
    var uuid = this.uuid,
        zcls = this.getZclass(),
        pageCount = (this.getKnownPageCount() ? this.getPageCount() : '?'); // unknown page count

    out.push('<div class="z-dlpaging"><div name="', uuid, '"', this.domAttrs_(), '>'); // main div

    // table with buttons
    out.push('<div class="z-dlpaging-button-table"><table', zUtl.cellps0,
        '><tbody><tr><td><table id="', uuid, '-first" name="', uuid, '-first"',
        zUtl.cellps0, ' class="', zcls, '-btn"><tbody><tr>',
        '<td class="', zcls, '-btn-l"></td>',
        '<td class="', zcls, '-btn-m"><em unselectable="on">',
        '<button type="button" class="', zcls, '-first"> </button></em></td>',
        '<td class="', zcls, '-btn-r"></td></tr></tbody></table></td>',
        '<td><table id="', uuid, '-prev" name="', uuid, '-prev"', zUtl.cellps0,
        ' class="', zcls, '-btn"><tbody><tr><td class="', zcls, '-btn-l"></td>',
        '<td class="', zcls, '-btn-m"><em unselectable="on"><button type="button" class="',
        zcls, '-prev"> </button></em></td><td class="', zcls, '-btn-r"></td>',
        '</tr></tbody></table></td><td><span class="', zcls, '-sep"></span></td>',
        '<td><span class="', zcls, '-text"></span></td><td><input id="',
        uuid, '-real" name="', uuid, '-real" type="text" class="', zcls,
        '-inp" value="', this.getActivePage() + 1, '" size="3"/></td>',
        '<td><span class="', zcls, '-text">/ ', pageCount, '</span></td>',
        '<td><span class="', zcls, '-sep"></span></td><td><table id="', uuid,
        '-next" name="', uuid, '-next"', zUtl.cellps0, ' class="', zcls, '-btn">',
        '<tbody><tr><td class="', zcls, '-btn-l"></td><td class="',
        zcls, '-btn-m"><em unselectable="on"><button type="button" class="',
        zcls, '-next"> </button></em></td><td class="', zcls, '-btn-r"></td>',
        '</tr></tbody></table></td><td><table id="', uuid, '-last" name="',
        uuid, '-last"', zUtl.cellps0, ' class="', zcls, '-btn"><tbody><tr>',
        '<td class="', zcls, '-btn-l"></td><td class="', zcls,
        '-btn-m"><em unselectable="on"><button type="button" class="', zcls,
        '-last"> </button></em></td><td class="', zcls, '-btn-r"></td>',
        '</tr></tbody></table></td></tr></tbody></table></div>');

    // any children
    out.push('<div class="z-dlpaging-aux-content">');
    this.redrawChildren(out);
    out.push('</div>');

    if (this.isDetailed()) out.push(this._infoTags());
    out.push('<br class="z-dlzklib-clear"/></div></div>');
}
