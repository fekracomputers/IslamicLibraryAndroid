var serializedHighlights = selectioniterface.getSerializedHighlights();
var highlighter;

function addNotedHighlight(className)
                 		{
                             highlighter.addClassApplier(rangy.createClassApplier(className, {
                                 ignoreWhiteSpace: true,

                 			    elementTagName: "a",
                 				elementProperties: {
                 				 href: "#$",
                                           onclick:highlight_clicked
                 				            }
                             }));
                 		}

                         window.onload = function() {

                        rangy.init();
                 		highlighter = rangy.createHighlighter();
                 		addNotedHighlight("highlight1");
                 		addNotedHighlight("highlight2");
                 		addNotedHighlight("highlight3");
                 		addNotedHighlight("highlight4");



                             highlighter.addClassApplier(rangy.createClassApplier("note", {
                                 ignoreWhiteSpace: true,
                                 elementTagName: "a",
                                 elementProperties: {
                                     href: "#",
                                     onclick: function() {
                                         var highlight = highlighter.getHighlightForElement(this);
                                         if (window.confirm("Delete this note (ID " + highlight.id + ")?")) {
                                             highlighter.removeHighlights( [highlight] );
                                         }
                                         return false;
                                     }
                                 }
                             }));


                             if (serializedHighlights) {
                                 highlighter.deserialize(serializedHighlights);
                             }
                         };

function highlight_clicked()
{
var highlight = highlighter.getHighlightForElement(this);
var sel = rangy.getSelection();
sel.setSingleRange(highlight.getRange());
selectioniterface.highlightClicked(highlight.id);

}

function copySelectedText()
{
 selectioniterface.copySelectedText(rangy.getSelection().getRangeAt(0).text(), rangy.getSelection().toHtml());
}

function shareSelectedText()
{
selectioniterface.shareSelectedText(rangy.getSelection().getRangeAt(0).text(),rangy.getSelection().toHtml());
}

function getSelectionRect()
{
//    var rect=rangy.getSelection().getRangeAt(0).getBoundingDocumentRect();
    var rect=rangy.getSelection().nativeSelection.getRangeAt(0).getBoundingClientRect();
    //(int left,int top,int right,int bottom)
    // selectioniterface.setSelectionRect(rect.left,rect.top,rect.right,rect.bottom);
     selectioniterface.setSelectionRect(rect.left,rect.top,rect.right,rect.bottom);
     //selectioniterface.jsDebug("rect:"+rect.left+","+rect.top+","+rect.right+","+rect.bottom)
     selectioniterface.jsDebug("rect:"+rect.left+","+rect.top+","+rect.right+","+rect.bottom)
}

function highlightSelectedText(i) {
    highlighter.highlightSelection("highlight" + i);
}

function noteSelectedText() {
    highlighter.highlightSelection("note");
}

function selectAll()
{
document.execCommand('selectall', true, null);
}

function removeHighlightFromSelectedText() {
    highlighter.unhighlightSelection();
}

function serializeHighlights() {
    selectioniterface.setSerializedHighlights(highlighter.serialize({serializeHighlightText: true}));
}

function setBackgroundColor(color) {
        document.body.style.backgroundColor = color;
}