// CrossMark Dialog Box v1.5

// Create crossmark object
var crossmark = {

	sOpenSelector: '#open-crossmark',
	sDOI: '',
	sDomain: '',
	sURIScheme: '',
	sCrossMarkURL: '',
	sStylesURL: '',
	sTooltipID: 'crossmark-tooltip-130',
	sTooltipSelector: '#crossmark-tooltip-130',
	sTooltipCopy: 'Click to get updates and verify authenticity.',
	scriptVersion: 'v1.5',
	versionDir: 'v1.5/',

	initialize: function() {
		this.sDOI = this.detectDOI();
		this.sDomain = window.location.hostname;
		this.sURIScheme = window.location.protocol;
		this.sCrossMarkURL = '{{consts.crossmark-server}}' + '/dialog/?doi=' + this.sDOI + '&domain=' + this.sDomain + '&uri_scheme=' + this.sURIScheme + '&cm_version=' + this.scriptVersion;
		this.sStylesURL = '{{consts.cdn-url}}' + '/stylesheets/' + this.versionDir + 'crossmark_widget.css';
	},
  
	addStylesheet: function() {
		$(this.sOpenSelector).parent().eq(0).prepend("<link media='screen' rel='stylesheet' type='text/css' href='" + this.sStylesURL + "'/>");
	},

	activateTooltip: function() {
		var that = this;
		$('body').append('<div id="' + this.sTooltipID + '" class="crossmark-tooltip" style="display: none;"><div class="cmtttop"></div><div class="cmttmid"><p>' + this.sTooltipCopy + '</p></div><div class="cmttbot"></div></div>');
		var $icon = $('#crossmark-icon');
		$icon.attr({'title': '', 'alt': ''}).show();
		$icon.mouseover(function(){
			var o = $icon.offset();
			var x = o.left + $icon.width()/2 - $('#crossmark-icon').width()/2;
			var y = o.top - $(that.sTooltipSelector).height() + 10;
			$(that.sTooltipSelector).css({ 'left' : x, 'top' : y }).show();
		});
		$icon.mouseout(function(){
			$(that.sTooltipSelector).hide();
		});
	},

	activateDialog: function() {
		var that = this;
		$(this.sOpenSelector).click(function(){
			$('#crossmark-dialog-frame').attr('src', that.sCrossMarkURL);
			$('#crossmark-dialog').dialog("open");
			$(that.sTooltipSelector).hide();
			return false;
		});
	},

	detectDOI: function() {
		this.sDOI = '';
		var dc_id = $('meta').filter(function() {
			var $this = $(this);
			var isIdentifier = (/dc\.identifier/i).test($this.attr('name'));
			var isDOI = (/^info:doi/i).test($this.attr('content')) ||
						(/^doi/i).test($this.attr('content')) ||
						(/^10\./i).test($this.attr('content')) ||
						(/dx.doi.org\./i).test($this.attr('content'));

			return isIdentifier && isDOI;
		}).attr("content");
		
		if (dc_id) {
			dc_id = dc_id.replace(/^info:doi\//, ''); // Nature style
			dc_id = dc_id.replace(/^doi:/, ''); // IngentaConnect style
		}

		return dc_id;
	}
};

jQuery(function($) {

	// Define CrossMark Dialog
	$('#crossmark-dialog').dialog({
		zIndex: 3999,
		autoOpen: false,
		// Set to true for PDF case
		modal: true,
		resizable: false,
		draggable: false,
		open: function() {
			$(".ui-widget-overlay").click(function(){
				$('#crossmark-dialog').dialog("close");
			});
		},
		beforeClose: function() {
			$(".ui-widget-overlay").unbind();
		},
		height: 550,
		width: 550,
		dialogClass: "crossmark-ui-dialog"
	});

	// Initialize Crossmark Object
	crossmark.initialize();
	crossmark.addStylesheet();
	crossmark.activateTooltip();
	crossmark.activateDialog();

});
