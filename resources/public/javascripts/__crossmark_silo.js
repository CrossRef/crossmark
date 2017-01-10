// CrossMark Dialog Box
// Uses jQuery UI 1.8.7 / jQuery 1.4.4

// Create crossmark object
var crossmark = {

	sCrossMarkServer: '{{consts.crossmark-server}}',
	sCDNServer: '{{consts.cdn-url}}',
	sOpenSelector: '#open-crossmark',
	sDOI: '',
	sDomain: '',
	sURIScheme: '',
	sCrossMarkURL: '',
	sStylesURL: '',
	sTooltipID: 'crossmark-tooltip-130',
	sTooltipSelector: '#crossmark-tooltip-130',
	sTooltipCopy: 'Click to get updates and verify authenticity.',

	initialize: function() {
		this.sDOI = this.detectDOI();
		this.sDomain = window.location.hostname;
		this.sURIScheme = window.location.protocol;
		this.sCrossMarkURL = this.sCrossMarkServer + '/dialog/?doi=' + this.sDOI + '&domain=' + this.sDomain + '&uri_scheme=' + this.sURIScheme;
		this.sStylesURL = this.sCDNServer + '/stylesheets/crossmark_widget.css';
	},

	addStylesheet: function() {
		$cmjq(this.sOpenSelector).parent().eq(0).prepend("<link media='screen' rel='stylesheet' type='text/css' href='" + this.sStylesURL + "'/>");
		//$cmjq('head').append("<link media='screen' rel='stylesheet' type='text/css' href='" + this.sStylesURL + "'/>");
	},

	activateTooltip: function() {
		var that = this;
		$cmjq('body').append('<div id="' + this.sTooltipID + '" class="crossmark-tooltip" style="display: none;"><div class="cmtttop"></div><div class="cmttmid"><p>' + this.sTooltipCopy + '</p></div><div class="cmttbot"></div></div>');
		$cmjq('#crossmark-icon').attr('title', '');
		$cmjq('#crossmark-icon').attr('alt', '');
		$cmjq('#crossmark-icon').show();
		$cmjq('#crossmark-icon').mouseover(function(){
			var x = $cmjq('#crossmark-icon').offset().left + $cmjq('#crossmark-icon').width()/2 - $cmjq('#crossmark-icon').width()/2;
			var y = $cmjq('#crossmark-icon').offset().top - $cmjq(that.sTooltipSelector).height() + 10;
			$cmjq(that.sTooltipSelector).css({ 'left' : x });
			$cmjq(that.sTooltipSelector).css({ 'top' : y });
			$cmjq(that.sTooltipSelector).show();
		});
		$cmjq('#crossmark-icon').mouseout(function(){
			$cmjq(that.sTooltipSelector).hide();
		});
	},

	activateDialog: function() {
		var that = this;
		$cmjq(this.sOpenSelector).click(function(){
			$cmjq('#crossmark-dialog-frame').attr('src', that.sCrossMarkURL);
			$cmjq('#crossmark-dialog').dialog("open");
			$cmjq(that.sTooltipSelector).hide();
			return false;
		});
	},

	detectDOI: function() {
		this.sDOI = '';
		var dc_id = $cmjq('meta').filter(function() {
			var $this = $cmjq(this);
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

var $cmjq = jQuery.noConflict();    // assign the main jQuery object to $cmjqj
$cmjq("#tabs-container").tabs();
	
jQuery(function($cmjq) {

	// Define CrossMark Dialog
	$cmjq('#crossmark-dialog').dialog({
		zIndex: 3999,
		autoOpen: false,
		// Set to true for PDF case
		modal: true,
		resizable: false,
		draggable: false,
		open: function() { 
			$cmjq(".ui-widget-overlay").click(function(){
				$cmjq('#crossmark-dialog').dialog("close");
			}); 
		},
		beforeClose: function() { 
			$cmjq(".ui-widget-overlay").unbind(); 
		},
		height: 550,
		width: 550
	});

	// Initialize Crossmark Object
	crossmark.initialize();
	crossmark.addStylesheet();
	crossmark.activateTooltip();
	crossmark.activateDialog();

});

