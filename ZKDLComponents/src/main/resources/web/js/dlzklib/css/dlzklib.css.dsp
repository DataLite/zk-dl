<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

/* global */
.z-dlzklib-clear { clear: both; }
.selectable { cursor : pointer!important; }
.nonselectable { cursor : default!important; }

/* -- Quickfilter component -- */
.z-quickfilter { font-size: 12px; margin-left: 7px; }
.z-quickfilter span { display: block; }
.z-quickfilter-text { float: left; margin: 3px 4px 0 0; }
.z-quickfilter-list { background: url(${c:encodeThemeURL('~./js/dlzklib/img/btn-open-list.png')}) no-repeat; cursor: pointer; float: left; height: 10px; width: 16px; margin: 9px 4px 0 0; }
.z-quickfilter-real { background: #fff; border: 1px solid #E6E6E6; border-top-color: #B2B2B2; float: left; padding: 2px 20px 2px 5px; }
.z-quickfilter-del { background: url(${c:encodeURL('~./js/dlzklib/img/btn-del-text.png')}) no-repeat; cursor: pointer; float: left; height: 16px; width: 16px; margin: 7px 0 0 -20px; position: relative; }
.z-quickfilter-magnifier { background: url(${c:encodeThemeURL('~./js/dlzklib/img/btn-magnifier.png')}) no-repeat; cursor: pointer; float: left; height: 16px; width: 16px; margin: 4px 0 0 3px; }

/* -- DLPaging component -- */
.z-dlpaging { height: auto; }
.z-dlpaging-button-table { float: left; margin-right: 30px; margin-left: 8px; width: auto; }
.z-dlpaging-aux-content { float: left; margin: 0 auto; padding-top: 2px; text-align: center; vertical-align: middle; width: auto; }
.z-paging-info, .z-paging div.z-paging-info { top: 8px; }

/* -- Listboxmanager component -- */
.z-listboxmanager { float: right; margin: 2px 2px 0 0; }
.z-listboxmanager td { padding: 0 3px; }
.z-listboxmanager span { cursor: pointer; display: block; height: 20px; width: 20px; }
.z-listboxmanager-menu_items_small { background: url(${c:encodeThemeURL('~./js/dlzklib/img/menu_items_small.png')}) no-repeat; }
.z-listboxmanager-sort_small { background: url(${c:encodeThemeURL('~./js/dlzklib/img/sort_small.png')}) no-repeat; }
.z-listboxmanager-filter_small { background: url(${c:encodeThemeURL('~./js/dlzklib/img/filter_small.png')}) no-repeat; }
.z-listboxmanager-filter_small_active { background: url(${c:encodeThemeURL('~./js/dlzklib/img/filter_small_active.png')}) no-repeat; }
.z-listboxmanager-cancel_filter { background: url(${c:encodeThemeURL('~./js/dlzklib/img/cancel_filter.png')}) no-repeat; }
.z-listboxmanager-excel_small { background: url(${c:encodeThemeURL('~./js/dlzklib/img/excel_small.png')}) no-repeat; }
.z-listboxmanager-trash_small { background: url(${c:encodeThemeURL('~./js/dlzklib/img/trash.png')}) no-repeat; }

/* -- Listcontrol component -- */
.z-listcontrol { height: 20px; padding: 2px 0px 7px 5px; }
.z-listcontrol-aux-content { float: left; height: 25px; width: auto; margin: 0 auto 0 30px; text-align: center; vertical-align: middle; }
.z-listcontrol .z-quickfilter { float: left; }

/* -- Lovbox component -- */
.z-lovbox { position: relative; }

/* -- Lovbox image -- */
.z-bandbox-label-btn { font-style: normal!important; height: 16px!important; padding: 2px 3px 1px 6px!important; }
.z-bandbox-label-btn .z-bandbox-image-btn { float: right; }

/* -- Lovbox popup -- */
.z-lovbox-pp { height: auto!important; width: auto!important; }
.z-lovbox-pp .z-bandpopup { background: #e2e2e2; padding: 3px 3px 3px 3px; height: auto!important; width: auto!important; }
.z-lovbox-pp .z-bandbox-popup-cl { border: 1px solid #7eaac6; }
.z-lovbox-pp .z-listbox { margin: 0 auto; overflow-y: auto; }
.z-lovbox-pp .z-dlpaging { padding: 0!important; }

.z-lovbox .z-bandbox-image-btn { background: url(${c:encodeThemeURL('~./zul/img/input/bandbtn.gif')}) left -1px; cursor: pointer; display: block; height: 16px; width: 14px; }
.z-lovbox .z-bandbox-image-btn.z-bandbox-btn-over { border: 0 none; margin-left: 0; }
.z-lovbox .z-bandbox-del, 
.z-lovbox .z-bandbox-rounded-del { background: url(${c:encodeURL('~./js/dlzklib/img/clear.png')}) no-repeat; cursor: pointer; display: none; padding: 5px 7px; position: absolute; right: 18px; top: 5px; }
.z-lovbox .z-bandbox-del:hover, 
.z-lovbox .z-bandbox-rounded-del:hover { background: url(${c:encodeURL('~./js/dlzklib/img/clear_huge.png')}) no-repeat; padding: 7px 7px; top: 4px; }

/* -- Login -- */
.z-login { background-image:none; background-color:#545E6B; font-size: 12px; margin: 0 auto; }
.z-login .z-login-header { color: white; font-weight: bold; padding: 7px 5px;  }
.z-login .z-login-lbl { display: block; margin-bottom: 3px; }
.z-login input[type=text],
.z-login input[type=password] { border: 1px solid #717171; font-size: 15px; outline: none; padding: 5px 30px 5px 5px; }
.z-login .z-image { position: relative; top: -22px; left: 176px; }
.z-login .z-login-cm { border: 1px solid #545E6B; padding: 20px 0 30px 0;  }
.z-login .z-login-cnt { margin: 0 auto; position: relative; width: 200px; }
.z-login .z-login-rememberme { display: block; margin-top: -10px; }
.z-login .z-login-rememberme input[type=checkbox] { margin-right: 5px; position: relative; top: 1px; }
.z-login .z-login-submit { border: 1px solid #C7C7C7; color: #000; cursor: pointer; margin-top: 15px; padding: 4px 0; text-shadow: 0 1px 1px #fff; width: 198px; }
.z-login .z-login-submit:hover { border-color: #8FB9D0; }
.z-login .z-login-error { color: red; margin-bottom: 15px; position: relative; left: -18px; width: 300px; }

/* -- Error 404 -- */
.z-404 { background:#fff; border: 1px solid #7eaac6; color: #666; font-size: 14px; margin: 20px auto 0 auto; }
.z-404 .z-404-header { border-bottom: 1px solid #BEBEBE; font-size: 12px; padding: 7px 5px;  }
.z-404 a { color: #545e6b; }
.z-404 a:hover { text-decoration: underline; }
.z-404 hr { border-color: #fff!important; margin-top: 30px;}
.z-404 p { margin: 5px 0; }
.z-404 h1.title { color: #666; font-size: 2em; font-weight: bold!important; margin-bottom: 5px; }
.z-404 .z-404-cm {  }
.z-404 .z-404-cnt { margin: 0 auto; padding: 10px 0 5px 0; position: relative; text-align: center; width: 500px; }
.z-404 .btn { border: 1px solid #ccc; border-bottom: 1px solid #b4b4b4; color: #666; margin-top: 15px; margin-bottom: 5px; padding: 6px 14px 20px 14px; text-shadow: 0 1px 1px #fff; width: auto; }
.z-404 .sad-smile { width: 100%; height: 128px; background: url(${c:encodeThemeURL('~./js/dlzklib/img/sad-smile_128x128.png')}) no-repeat; background-position:center; }

/* -- Critical error 500 (error.zul) -- */
.z-criticalError { background:#fff; border: 1px solid #7eaac6; color: #666; font-size: 14px; margin: 0 auto; overflow: hidden; z-index: 1900!important; }
.z-criticalError .z-criticalError-header { border-bottom: 1px solid #BEBEBE; font-size: 12px; padding: 7px 5px;  }
.z-criticalError a { color: #545e6b; }
.z-criticalError a:hover { text-decoration: underline; }
.z-criticalError hr { border-color: #fff!important; margin-top: 30px;}
.z-criticalError p { font-size: 11px; margin: 5px 0; }
.z-criticalError h1.title { color: #666; font-size: 2em; font-weight: bold!important; line-height: 1; margin-bottom: 10px; }
.z-criticalError .z-criticalError-cnt { margin: 0 auto; padding: 10px 0 5px 0; position: relative; text-align: center; width: 100%; }
.z-criticalError-textarea { width: 840px; }
.z-criticalError-download { color: red; cursor: pointer; font-weight: bold; margin-right: 7px; }
.z-criticalError-download:hover { text-decoration:underline; }
.z-criticalError .globe { cursor: pointer; position: relative; top: 5px; }
.z-criticalError .btn { border: 1px solid #ccc; border-bottom: 1px solid #b4b4b4; color: #666; margin-top: 15px; margin-bottom: 5px; padding: 6px 14px 20px 14px; text-shadow: 0 1px 1px #fff; width: auto; }

/* -- CSS3 -- */
.z-quickfilter-real,
.z-login input[type=text],
.z-login input[type=password],
.z-login input[type=text]:focus,
.z-login input[type=password]:focus,
.z-login .z-login-cm,
.z-login .z-login-submit,
.z-login .z-login-submit:hover,
.z-404,
.z-404 .z-404-header,
.z-404 .btn,
.z-criticalError,
.z-criticalError .z-criticalError-header,
.z-criticalError .btn { -ms-behavior: url('css/PIE/PIE.htc'); position: relative; }

/* border-radius */
.z-quickfilter-real {
-moz-border-radius: 2px 2px 2px 2px;
-webkit-border-radius: 2px 2px 2px 2px; 
border-radius: 2px 2px 2px 2px;
}
.z-login .z-login-submit,
.z-404,
.z-404 .btn,
.z-criticalError,
.z-criticalError .btn {
-webkit-border-radius: 5px 5px 5px 5px;
-moz-border-radius: 5px 5px 5px 5px;
border-radius: 5px 5px 5px 5px;
}
.z-404 .z-404-header,
.z-criticalError .z-criticalError-header {
-webkit-border-radius: 5px 5px 0 0;
-moz-border-radius: 5px 5px 0 0;
border-radius: 5px 5px 0 0;
}

/* gradient */
.z-login input[type=text],
.z-login input[type=password] {
background: -moz-linear-gradient(top, rgba(201,201,202,1) 0%, rgba(255,255,255,0.79) 70%, rgba(255,255,255,1) 100%);
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,rgba(201,201,202,1)), color-stop(70%,rgba(255,255,255,0.79)), color-stop(100%,rgba(255,255,255,1)));
background: -webkit-linear-gradient(top, rgba(201,201,202,1) 0%,rgba(255,255,255,0.79) 70%,rgba(255,255,255,1) 100%);
background: -o-linear-gradient(top, rgba(201,201,202,1) 0%,rgba(255,255,255,0.79) 70%,rgba(255,255,255,1) 100%);
background: -ms-linear-gradient(top, rgba(201,201,202,1) 0%,rgba(255,255,255,0.79) 70%,rgba(255,255,255,1) 100%);
background: linear-gradient(top, rgba(201,201,202,1) 0%,rgba(255,255,255,0.79) 70%,rgba(255,255,255,1) 100%);
-pie-background: linear-gradient(top, rgba(201,201,202,1) 0%,rgba(255,255,255,0.79) 70%,rgba(255,255,255,1) 100%);
}
.z-login .z-login-cm {
background: #e2e2e2;
background: -moz-linear-gradient(top, #e2e2e2 0%, #fafafa 100%);
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#e2e2e2), color-stop(100%,#fafafa));
background: -webkit-linear-gradient(top, #e2e2e2 0%,#fafafa 100%);
background: -o-linear-gradient(top, #e2e2e2 0%,#fafafa 100%);
background: -ms-linear-gradient(top, #e2e2e2 0%,#fafafa 100%);
background: linear-gradient(top, #e2e2e2 0%,#fafafa 100%);
-pie-background: linear-gradient(top, #e2e2e2 0%,#fafafa 100%);
}
.z-login .z-login-submit {
background: #ffffff;
background: -moz-linear-gradient(top, #ffffff 0%, #d7d7d7 100%);
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#ffffff), color-stop(100%,#d7d7d7));
background: -webkit-linear-gradient(top, #ffffff 0%,#d7d7d7 100%);
background: -o-linear-gradient(top, #ffffff 0%,#d7d7d7 100%);
background: -ms-linear-gradient(top, #ffffff 0%,#d7d7d7 100%);
background: linear-gradient(top, #ffffff 0%,#d7d7d7 100%);
-pie-background: linear-gradient(top, #ffffff 0%,#d7d7d7 100%);
}
.z-login .z-login-submit:hover {
background: #f6fcfe;
background: -moz-linear-gradient(top, #f6fcfe 0%, #badbec 100%);
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#f6fcfe), color-stop(100%,#badbec));
background: -webkit-linear-gradient(top, #f6fcfe 0%,#badbec 100%);
background: -o-linear-gradient(top, #f6fcfe 0%,#badbec 100%);
background: -ms-linear-gradient(top, #f6fcfe 0%,#badbec 100%);
background: linear-gradient(top, #f6fcfe 0%,#badbec 100%);
-pie-background: linear-gradient(top, #f6fcfe 0%,#badbec 100%);
}
.z-404 .z-404-header {
background: #f6f8f9;
background: -moz-linear-gradient(top, #f6f8f9 0%, #e5ebee 50%, #d7dee3 51%, #f5f7f9 100%)!important;
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#f6f8f9), color-stop(50%,#e5ebee), color-stop(51%,#d7dee3), color-stop(100%,#f5f7f9))!important;
background: -webkit-linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
background: -o-linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
background: -ms-linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
background: linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
-pie-background: linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
}
.z-404 .btn {
background: #f3f3f3;
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #f3f3f3), color-stop(50%, #dddddd), color-stop(50%, #d2d2d2), color-stop(100%, #dfdfdf));
background: -webkit-linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
background: -moz-linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
background: -ms-linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
background: -o-linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
background: linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
-pie-background: linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
}
.z-404 .btn:hover {
background: #e5e5e5;
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #e5e5e5), color-stop(50%, #d1d1d1), color-stop(50%, #c4c4c4), color-stop(100%, #b8b8b8));
background: -webkit-linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
background: -moz-linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
background: -ms-linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
background: -o-linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
background: linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
-pie-background: linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
}
.z-criticalError .z-criticalError-header {
background: #f6f8f9;
background: -moz-linear-gradient(top, #f6f8f9 0%, #e5ebee 50%, #d7dee3 51%, #f5f7f9 100%)!important;
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#f6f8f9), color-stop(50%,#e5ebee), color-stop(51%,#d7dee3), color-stop(100%,#f5f7f9))!important;
background: -webkit-linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
background: -o-linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
background: -ms-linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
background: linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
-pie-background: linear-gradient(top, #f6f8f9 0%,#e5ebee 50%,#d7dee3 51%,#f5f7f9 100%)!important;
}
.z-criticalError .btn {
background: #f3f3f3;
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #f3f3f3), color-stop(50%, #dddddd), color-stop(50%, #d2d2d2), color-stop(100%, #dfdfdf));
background: -webkit-linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
background: -moz-linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
background: -ms-linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
background: -o-linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
background: linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
-pie-background: linear-gradient(top, #f3f3f3 0%, #dddddd 50%, #d2d2d2 50%, #dfdfdf 100%);
}
.z-criticalError .btn:hover {
background: #e5e5e5;
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #e5e5e5), color-stop(50%, #d1d1d1), color-stop(50%, #c4c4c4), color-stop(100%, #b8b8b8));
background: -webkit-linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
background: -moz-linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
background: -ms-linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
background: -o-linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
background: linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
-pie-background: linear-gradient(top, #e5e5e5 0%, #d1d1d1 50%, #c4c4c4 50%, #b8b8b8 100%);
}

/* box-shadow */
.z-login .z-login-submit:active {
-moz-box-shadow: inset 0 0 10px 0 #6ba8e5, 0 1px 0 0 white;
-webkit-box-shadow: inset 0 0 10px 0 #6ba8e5, 0 1px 0 0 white;
box-shadow: inset 0 0 10px 0 #6ba8e5, 0 1px 0 0 white;
}
.z-login input[type=text]:focus,
.z-login input[type=password]:focus {
-moz-box-shadow: 0px 0px 5px #007eff;
-webkit-box-shadow: 0px 0px 5px #007eff;
box-shadow: 0px 0px 5px #007eff;
}
.z-login input.error {
-moz-box-shadow: 0px 0px 5px red;
-webkit-box-shadow: 0px 0px 5px red;
box-shadow: 0px 0px 5px red;
}
.z-login input.error:focus {
-moz-box-shadow: 0px 0px 5px #007eff;
-webkit-box-shadow: 0px 0px 5px #007eff;
box-shadow: 0px 0px 5px #007eff;
}
.z-404 .btn:active,
.z-criticalError .btn:active {
-moz-box-shadow: inset 0 0 30px 0 #999999, 0 1px 0 0 white;
-webkit-box-shadow: inset 0 0 30px 0 #999999, 0 1px 0 0 white;
box-shadow: inset 0 0 30px 0 #999999, 0 1px 0 0 white;
}