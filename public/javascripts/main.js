$(function() {
    // Pre-filled hints for input fields.
    $("input.unfilled,textarea.unfilled").live("focus", function() {
        $(this).data("prefillValue", $(this).val());
        $(this).val(""); 
        $(this).removeClass("unfilled");
        $(this).addClass("filled");
    });
    $("input.filled,textarea.filled").live("blur", function() {
        if ($(this).val() == "") {
            $(this).val($(this).data("prefillValue")); 
            $(this).removeClass("filled");
            $(this).addClass("unfilled");
        }
    });
    // Clear unfilled fields before form submission.
    $("form.fragment").submit(function() {
       $(this).find("input.unfilled,textarea.unfilled").each(function() {
          $(this).val(""); 
       });
    });
});