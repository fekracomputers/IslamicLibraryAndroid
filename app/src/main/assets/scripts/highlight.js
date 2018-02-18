var serializedHighlights = selectioniterface.getSerializedHighlights();
var searchQuery= selectioniterface.getSearchQuery();
var highlighter;
var searchResultApplier;

function addNotedHighlight(className) {
    highlighter.addClassApplier(rangy.createClassApplier(className, {
        ignoreWhiteSpace: true,
        elementTagName: "a",
        elementProperties: {
            href: "#$",
            onclick: highlight_clicked
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

    if (serializedHighlights) {
        highlighter.deserialize(serializedHighlights);
    }
    searchResultApplier = rangy.createClassApplier("searchResult");

    if(searchQuery){
        highlightSearchString(searchQuery);
    }
}
;

function highlight_clicked() {
    var highlight = highlighter.getHighlightForElement(this);
    var sel = rangy.getSelection();
    sel.setSingleRange(highlight.getRange());
    selectioniterface.highlightClicked(highlight.id);

}

function copySelectedText() {
    selectioniterface.copySelectedText(rangy.getSelection().getRangeAt(0).text(), rangy.getSelection().toHtml());
}

function shareSelectedText() {
    selectioniterface.shareSelectedText(rangy.getSelection().getRangeAt(0).text(), rangy.getSelection().toHtml());
}

function getSelectionRect() {
    //    var rect=rangy.getSelection().getRangeAt(0).getBoundingDocumentRect();
    var rect = rangy.getSelection().nativeSelection.getRangeAt(0).getBoundingClientRect();
    //(int left,int top,int right,int bottom)
    // selectioniterface.setSelectionRect(rect.left,rect.top,rect.right,rect.bottom);
    selectioniterface.setSelectionRect(rect.left, rect.top, rect.right, rect.bottom);
    //selectioniterface.jsDebug("rect:"+rect.left+","+rect.top+","+rect.right+","+rect.bottom)
    selectioniterface.jsDebug("rect:" + rect.left + "," + rect.top + "," + rect.right + "," + rect.bottom)
}

function highlightSelectedText(i) {
    highlighter.highlightSelection("highlight" + i);
}

function noteSelectedText() {
    highlighter.highlightSelection("note");
}

function selectAll() {
    document.execCommand('selectall', true, null);
}

function removeHighlightFromSelectedText() {
    highlighter.unhighlightSelection();
}

function serializeHighlights() {
    selectioniterface.setSerializedHighlights(highlighter.serialize({
        serializeHighlightText: true
    }));
}

function setBackgroundColor(color) {
    document.body.style.backgroundColor = color;
}

function setHeadingColor(color) {
    document.querySelectorAll("h1,h2,h3,h4,h5,h6").forEach(function(header) {
        header.style.color = color;
    });
}

function setTextColor(color) {
    document.body.style.color = color;
}

function prepareRegex(searchTerm) {
    var searchtermTashkeelHande=searchTerm.replace(new RegExp("[گء-كم-ولىي]","g"),"$&[ًٌٍَُِّْ]*");
    var searchtermYalHande=searchtermTashkeelHande.replace(/ي/g,"[ي|ى]");
    var searchtermAlefHande=searchtermYalHande.replace(/ا/g,"[أ|إ|آ|ا]");
    var searchtermTaHande=searchtermAlefHande.replace(/ه/g,"[ة|ه]");

    return new RegExp(searchtermTaHande,"g");
}
function removeSearchResultHighlights(){
        var range = rangy.createRange();
        range.selectNodeContents(document.body);
        searchResultApplier.undoToRange(range);

}
function highlightSearchterms(searchTerms) {
        // Remove existing highlights
        var range = rangy.createRange();
        var searchScopeRange = rangy.createRange();
        searchScopeRange.selectNodeContents(document.body);

        var options = {
            caseSensitive: false,
            wholeWordsOnly: false,
            withinRange: searchScopeRange,
            direction: "forward"// This is redundant because "forward" is the default
        };

        range.selectNodeContents(document.body);
        searchResultApplier.undoToRange(range);
        if (searchTerms) {
            searchTerms.forEach(function(searchTerm) {
                if (searchTerm != "") {
                    searchTerm = prepareRegex(searchTerm);

                    range.selectNodeContents(document.body)

                    // Iterate over matches
                    while (range.findText(searchTerm, options)) {
                        // range now encompasses the first text match
                        searchResultApplier.applyToRange(range);

                        // Collapse the range to the position immediately after the match
                        range.collapse(false);
                    }
                }
            });
        }

}

function highlightSearchString(s) {
    highlightSearchterms(s.split(" "));
}