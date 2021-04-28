/** Function to attach the confirm delete dialog **/
function attachConfirmDelete(confirmMessage) {
    $(".confirmDelete").click(function () {
        return confirm(confirmMessage);
    });
}

function attachConfirmDeleteClass(className, confirmMessage) {
    $("." + className).click(function () {
        return confirm(confirmMessage);
    });
}

/** Function to attach the published / unpublished Ajax method **/
function attachAjaxTogglePublished(publishAlt, unpublishAlt, contextPath) {
    $(".publishLinkAjax").click(function (e) {
        e.preventDefault();
        var url = this.href;

        $.get(url, null, function (xml) {

            if (xml == "false") {
                e.target.src = contextPath + "/images/button-disabled.png";
                e.target.title = publishAlt;
                e.target.alt = publishAlt;
            }
            else if (xml == "true") {
                e.target.src = contextPath + "/images/button-ok.png";
                e.target.title = unpublishAlt;
                e.target.alt = unpublishAlt;
            }
            else {
                // error ocurred
            }
        });

        return false;
    });
}

/** Function to clean field then a onchange event occures, and flush autocomplete cache **/
function cleanFieldOnChangeEvent(fieldEventId, fieldToCleanId) {
    $("#" + fieldEventId).change(function () {
        $('#' + fieldToCleanId).val('');
        $('#' + fieldToCleanId).flushCache();
    });
}

function onkeyPressFileInput(e) {
    var key = window.event ? e.keyCode : e.which;
    if (key != null && key != '') {
        return false;
    }
    return true;
}

<!--Disable input form buttons after submit-->
var forms = document.getElementsByTagName('form');
if (forms) {
    for (var i = 0; i < (forms.length); i++) {
        var form = document.getElementsByTagName('form')[i];
        if (form) {
            if (!form.onsubmit) {
                form.onsubmit = function () {
                    for (var j = 0; j < (form.getElementsByTagName('input').length); j++) {
                        if (form.getElementsByTagName('input')[j].type == 'button') {
                            form.getElementsByTagName('input')[j].disabled = true;
                        } else if (form.getElementsByTagName('input')[j].type == 'submit') {
                            var hiddenSubmit = document.createElement('input');
                            hiddenSubmit.setAttribute('type', 'hidden');
                            hiddenSubmit.setAttribute('name', form.getElementsByTagName('input')[j].name);
                            form.appendChild(hiddenSubmit);
                            form.getElementsByTagName('input')[j].disabled = true;
                        }
                    }
                };
            }
        }
    }
}


var counter = 0;
function addRow(contextPath, drTitle, documentType) {

    var newIdRemove = counter;
    var newDocumentTypeName = 'legalDocument.associatedLegalDocuments[' + counter + '].documentType.id';
    var newDocumentTypeId = 'documentTypeTemplate' + counter;
    var newTitleAssociationName = 'legalDocument.associatedLegalDocuments[' + counter + '].drTitle'
    var newTitleAssociationId = 'titleTemplate' + counter;
    var divTitleTemplateId = 'divTitleTemplate' + counter;

    var html = $('#template').html();

    html = html.replace("titleTemplateId", newTitleAssociationId);
    html = html.replace("titleTemplateName", newTitleAssociationName);
    html = html.replace("legalDocumentAssociationTemplate", 'legalDocumentAssociation' + counter);
    html = html.replace("removeRow", newIdRemove);
    html = html.replace("documentTypeTemplate", newDocumentTypeId);
    html =
        html.replace("documentTypeTemplateName", newDocumentTypeName);
    html = html.replace("divTitleTemplate", divTitleTemplateId);

    $('#legalDocumentAssociations').append(html);

    $('#' + divTitleTemplateId).append(' ' + (counter + 1));

    // set value in titleTemplate field
    if (drTitle != null) {
        document.getElementById('titleTemplate' + counter).value = drTitle;
    }

    //set value in documentType
    if (documentType != null) {
        $("#" + newDocumentTypeId + " option").each(function () {
            if ($(this).val() == documentType) {
                this.selected = 'selected';
            }
        });
    }

    //Initialize Auto complete
    $('#titleTemplate'
        + counter).autocomplete(contextPath + '/legislation/Legislation.action?autoCompleteInsertLegislation=', {
        minChars: 0,
        scroll: true,
        maxItemsToShow: 30,
        extraParams: {
            "legalDocumentTypeId": function () {
                return $("#" + newDocumentTypeId).val();
            },
            "searchField": function () {
                return $("#" + newTitleAssociationId).val();
            }
        }
    });

    cleanFieldOnChangeEvent(newDocumentTypeId, newTitleAssociationId)

    /* Remove Div*/
    $('#' + newIdRemove).click(function (e) {
        $('#legalDocumentAssociation' + e.target.id).remove();

    });

    counter++;
}

function attachContractCheckbox() {
    $(".checkboxContract").click(function () {
        var contractId = $(this).attr('id');

        if ($('#' + contractId).is(':checked')) {
            $('.' + contractId).addClass('contractSelected');
        } else {
            $('.' + contractId).removeClass('contractSelected');
        }
    });
    // check if checkboxes are selected and put the background accordingly
    $(".checkboxContract:checked").each(function () {
        var contractId = $(this).attr('id');
        $('.' + contractId).addClass('contractSelected');
    });
}

function attachUserValidityCheckbox() {
    $(".checkboxValidity").click(function (e) {
        var id = this.id;
        var startDate = $("#startDate" + id);
        var endDate = $("#endDate" + id);

        if (this.checked) {
            var valueStartDate = $("#contractStartDate" + id).html();
            var valueEndDate = $("#contractEndDate" + id).html();
            startDate.val(valueStartDate);
            endDate.val(valueEndDate);
        }
        else {
            startDate.val("");
            endDate.val("");
        }
    });
}

function checkClientPeiManagerStatus() {

    if ($('.clientpeimanagerCheck').attr("checked")) {
        $('.specialPermission').each(function () {
            $(this).attr("disabled", false);
        });

    } else {
        $('.specialPermission').each(function () {
            $(this).attr("disabled", true);
            $(this).attr("checked", false);
        });
    }
    //each(function (i) {
    /*if ($('.clientpeimanagerCheck').attr("checked")) {
     $('.specialPermission').attr("disabled", false);

     } else {
     $('.specialPermission').attr("disabled", true);
     $('.specialPermission').attr("checked", false);
     }*/
}

function attachOnChangeToProfile() {
    $('.clientpeimanagerCheck').click(function () {
        checkClientPeiManagerStatus();
    });
    checkClientPeiManagerStatus();
}

function attachViewUserLink(confirmeDeleteMessage) {

    $(".userSelectionLink").click(function (e) {
        e.preventDefault();
        var url = this.href;

        var xhr = $.get(url, null, function (data) {
            if (xhr.getResponseHeader('Stripes-Success') == "OK") {
                $('#userId').html(data);

                // change links
                $("#aditionalLinks").empty();
                $("#linkEdit").clone().appendTo($("#aditionalLinks"));
                $("#linkDelete").clone().appendTo($("#aditionalLinks"));
                $("#linkReset").clone().appendTo($("#aditionalLinks"));

                // attach deleteConfirm
                attachConfirmDelete(confirmeDeleteMessage);
            } else {
                window.location.reload(true);
            }
        });

        // Make the user selected in the left column
        $(".userSelectionLink").each(function (i) {
            $(this).removeClass("selected");
        });

        $(this).addClass("selected");

        return false;
    });
}

function loadUser(id, confirmDeleteMessage) {

    var user = $("#user" + id);
    var url = user.get(0).href;

    var xhr = $.get(url, null, function (data) {
        if (xhr.getResponseHeader('Stripes-Success') == "OK") {
            $('#userId').html(data);

            // change links
            $("#aditionalLinks").empty();
            $("#linkEdit").clone().appendTo($("#aditionalLinks"));
            $("#linkDelete").clone().appendTo($("#aditionalLinks"));
            $("#linkReset").clone().appendTo($("#aditionalLinks"));

            // attach deleteConfirm
            attachConfirmDelete(confirmDeleteMessage);
        } else {
            window.location.reload(true);
        }
    });

    // Make the user selected in the left column
    $(".userSelectionLink").each(function (i) {
        $(user).removeClass("selected");
    });

    $(user).addClass("selected");

}

function attachViewCompanyLink(confirmDeleteMessage, confirmDeleteContractMessage) {

    $(".companySelectionLink").click(function (e) {
        e.preventDefault();
        var url = this.href;

        var xhr = $.get(url, null, function (data) {
            if (xhr.getResponseHeader('Stripes-Success') == "OK") {
                $('#companyId').html(data);

                // change links
                $("#aditionalLinks").empty();
                $(".copyCompany").clone().appendTo($("#aditionalLinks"));

                // attach deleteConfirm
                attachConfirmDelete(confirmDeleteMessage);
                attachConfirmDeleteClass("confirmDeleteContract", confirmDeleteContractMessage);

            } else {
                window.location.reload(true);
            }
        });

        // Make the user selected in the left column
        $(".companySelectionLink").each(function (i) {
            $(this).removeClass("selected");
        });

        $(this).addClass("selected");

        return false;
    });

}

function loadCompany(id, confirmDeleteMessage, confirmDeleteContractMessage) {
    var user = $("#company" + id);
    var url = user.get(0).href;

    var xhr = $.get(url, null, function (data) {
        if (xhr.getResponseHeader('Stripes-Success') == "OK") {
            $('#companyId').html(data);

            // change links
            $("#aditionalLinks").empty();
            $(".copyCompany").clone().appendTo($("#aditionalLinks"));

            // attach deleteConfirm
            attachConfirmDelete(confirmDeleteMessage);
            attachConfirmDeleteClass("confirmDeleteContract", confirmDeleteContractMessage);
        } else {
            window.location.reload(true);
        }
    });

    // Make the user selected in the left column
    $(".companySelectionLink").each(function (i) {
        $(user).removeClass("selected");
    });

    $(user).addClass("selected");

}

function checkAndChangeLegalDocumentNotification() {
    if ($("#sendNotificationNew").attr("checked")) {
        $('#sendNotificationChange').attr("disabled", true);

    } else if ($("#sendNotificationChange").attr("checked")) {
        $('#sendNotificationNew').attr("disabled", true);
    }
}

function addUpdateLegalDocumentChangeEvent(update) {

    if (update) {
        checkAndChangeLegalDocumentNotification();

        $("#sendNotificationNew").click(function (e) {

            if (e.target.checked) {
                $('#sendNotificationChange').attr("disabled", true);

            } else {
                $('#sendNotificationChange').attr("disabled", false);
            }
        });

        $("#sendNotificationChange").click(function (e) {

            if (e.target.checked) {
                $('#sendNotificationNew').attr("disabled", true);

            } else {
                $('#sendNotificationNew').attr("disabled", false);
            }
        });
    }
}

function selectCheckbox(update) {

    if (update) {
        $('#documentState').change(function () {
            $('#sendNotificationNew').attr("disabled", true);
            $('#sendNotificationNew').attr("checked", false);
            $('#sendNotificationChange').attr("disabled", false);
            $('#sendNotificationChange').attr("checked", true);

        });
    }
}

function setFocus(id) {
    $('#' + id).focus();
}


/*       PEI         */

function getNodeLevel(key) {
    var split = key.split("/");
    return split.length;
}

function getSelectedTreeNodePath(isInIFrame) {
    var dtnode;
    if (isInIFrame) {
        dtnode = parent.$("#tree").dynatree("getActiveNode");
    } else {
        dtnode = $("#tree").dynatree("getActiveNode");
    }
    return encodeURI(dtnode.data.key);
}

function insertFolderForm(contextPath, moduleType) {
    $('#peiPropertyContent').attr("src", contextPath + "/plan/PlanCMOperations.action?planModuleType=" + moduleType
        + "&insertFolderForm=&folderId="
        + getSelectedTreeNodePath(false));
}

function insertFolderWithTemplate11MirrorForm(contextPath, moduleType) {
    $('#peiPropertyContent').attr("src", contextPath
        + "/plan/PlanCMOperations.action?planModuleType=" + moduleType
        + "&insertFolderWithTemplate11MirrorForm=&folderId="
        + getSelectedTreeNodePath(false));
}

function expandFolder(dtnode, contextPath, moduleType) {
    var encodedFolderId = encodeURI(dtnode.data.key);
    var xhr = $.post(contextPath
        + "/plan/PlanCMOperations.action?planModuleType=" + moduleType
        + "&expandFolder=", {folderId: encodedFolderId}, function (data) {
        if (xhr.getResponseHeader('Stripes-Success') == "OK") {
            var result = eval(data);
            dtnode.append(result);
            dtnode.setLazyNodeStatus(DTNodeStatus_Ok);
        } else {
            /* Other problem*/
            window.location.reload(true);
        }
    });
}

function deleteFolder(text, url, moduleType) {
    var result = window.confirm(text);

    if (result) {
        $('#peiPropertyContent').attr("src", url + "/plan/PlanCMOperations.action?planModuleType=" + moduleType
            + "&deleteFolder=&folderId="
            + getSelectedTreeNodePath(false));
    }
}

function attachOnChangeToCompanies(url) {
    $('#companies').change(function () {
        $.get(url, {companyId: $(this).val()}, function (j) {
            var result = eval(j);
            var options = '';

            for (var i = 0; i < result.length; i++) {
                options += '<option value="' + result[i].id + '">' + result[i].contractDesignation + '</option>';
            }

            $('#contractsLabel').html(options);
            $('#contractForm').submit();
        });
    });
}

function attachOnChangeToContracts() {
    $('#contractsLabel').change(function () {
        $('#contractForm').submit();
    });
}

function attachOnChangePeiMainCompanies(url) {
    $('#companies').change(function () {
        $.get(url, {companyId: $(this).val()}, function (j) {
            var result = eval(j);
            var options = '';

            for (var i = 0; i < result.length; i++) {
                options += '<option value="' + result[i].id + '">' + result[i].contractDesignation + '</option>';
            }

            $('#contractsLabel').html(options);
        });
    });
}

/* Get Folder Properties*/
function loadFolderInformation(dtnode, contextPath, moduleType) {
    var encodedFolderId = encodeURI(dtnode.data.key);
    $('#peiPropertyContent')
        .attr("src", contextPath + "/plan/PlanCMOperations.action?planModuleType=" + moduleType
        + "&loadFolderInformation=&folderId="
        + encodedFolderId);
}

function manageFolderLinks(dtnode) {
    /* See node level and active /deactivate folders links*/
    var level = getNodeLevel(dtnode.data.key);
    //root node
    if (level <= 3) {
        $('#addFolder').hide();
        $('#deleteFolder').hide();
        $('#addFolderWithTemplate11Mirror').hide();

    } else if (level == 5) {
        //first level node
        $('#addFolder').show();
        $('#deleteFolder').hide();
        $('#addFolderWithTemplate11Mirror').show();

    } else if (level >= 7) {
        $('#addFolder').show();
        $('#deleteFolder').show();
        $('#addFolderWithTemplate11Mirror').show();
    }
}

function initTreePEIAdmin(contextPath, moduleType) {
    $("#tree").dynatree({
        fx: {height: "toggle", duration: 200},
        //autoCollapse:true,
        onActivate: function (dtnode) {
            /* See node level and active /deactivate folders links*/
            manageFolderLinks(dtnode);
            /* Get Folder Properties*/
            loadFolderInformation(dtnode, contextPath, moduleType);
        },
        onLazyRead: function (dtnode) {
            expandFolder(dtnode, contextPath, moduleType);
        }
    });
}

/** PEI Main page **/
function loadPEIMenuAccordion() {
    $('.peiSelectionList').hSlides({
        totalWidth: 970,
        totalHeight: 340,
        minPanelWidth: 30,
        maxPanelWidth: 680,
        midPanelWidth: 150,
        eventHandler: 'hover',
        timeout: 0
    });

    $('.containerAccordion .sectionActive .content').click(function (e) {
        window.location.href = $(this).children("span").text();
        return false;
    });

    $('.containerAccordion .sessionActive').hover(function (e) {
        $(this).css('background-color', '#EFF3FF');
    }, function (e) {
        $(this).css('background-color', '#FFF');
    });

}

function attachEventToCopyPEI(id, contextPath, text, moduleType) {
    $('#' + id).click(function () {
        var result = window.confirm(text);

        if (result) {
            parent.$('#modalBoxCopyPEI').modal();
            var xhr = $.post(contextPath
                + "/plan/PlanCMOperations.action?copyOfflineToOnline=", {
                planModuleType: moduleType,
                folderId: getSelectedTreeNodePath(true)
            }, function (data) {

                //Load Modal Box
                if (xhr.getResponseHeader('Stripes-Success') == "OK") {
                    parent.$.modal.close();
                    $('#pei-info').html(data);
                    $("#exportOnline").removeAttr("disabled");
                    $("#exportOnline").css('background-color', '#2772B6');
                } else {
                    parent.$.modal.close();
                    top.location = contextPath + "/error.jsp";
                }
            });
        }
    });
    return false;
}

function attachEventToExportPlan(id, contextPath, moduleType, exportId, exportOnline) {
    $('#' + id).click(function () {
        var params = "&exportId=" + getSelectedTreeNodePath(true);
        params += "&planModuleType=" + moduleType;

        $.get(contextPath + "/plan/PlanCMOperations.action?planExportForm=", params, function (data) {
            window.parent.$('#exportModalBox').html(data);
            window.parent.$('#exportModalBox').modal({
                close: true,
                closeHTML: '<a class="modalCloseImg" title="Close"></a>',
                containerCss: {height: 'auto'}
            });
        });
    });
}

function changeTemplateTabStatus(templateIndexValue, templateFaqValue, templateDocumentsValue, templateContacts,
                                 templateProcedure, templateMeansResources) {
    if ($('#template').attr("value") == templateIndexValue || $('#template').attr("value") == templateFaqValue
        || $('#template').attr("value") == templateDocumentsValue
        || $('#template').attr("value") == templateContacts
        || $('#template').attr("value") == templateProcedure || $('#template').attr("value") == templateMeansResources) {
        $('#tabs').tabs('disable', 1)

    } else {
        $('#tabs').tabs('enable', 1)
    }
}

function attachOnChangeToTemplate(templateIndexValue, templateFaqValue, templateDocumentsValue, templateContacts,
                                  templateProcedure, templateMeansResources) {
    $('#template').change(function () {
        changeTemplateTabStatus(templateIndexValue, templateFaqValue, templateDocumentsValue, templateContacts,
            templateProcedure, templateMeansResources);
    });
}

function changeSelectedTab(tabsElement, selectIndex, contextPath, insertFolderFlagFlag, loadAjaxAdditionalInfo,
                           moduleType) {
    tabsElement.tabs('select', selectIndex); // switch tab

    if (loadAjaxAdditionalInfo) {
        if (selectIndex == 2) {
            loadAdditionalFolderText(contextPath, insertFolderFlagFlag, moduleType);
        }
    }
}

function setInRequestSelectedTab() {
    var selectedTab = $('#tabs').tabs('option', 'selected');
    $('#tabToOpen').val(selectedTab);
}

function loadAdditionalFolderText(contextPath, insertFolderFlagValue, moduleType) {
    if (!alreadyLoadedAdditionalContent) {
        //additionalTemplateText
        $('#ajaxLoading').show();
        $.post(contextPath
            +
            "/plan/PlanCMOperations.action?loadAdditionalFolderInfo=", {
            planModuleType: moduleType,
            folderId: getSelectedTreeNodePath(true),
            insertFolderFlag: insertFolderFlagValue
        }, function (data) {

            $('#additionalTemplateText').html(data);
            iResizeInsideIFrame();
        });
        alreadyLoadedAdditionalContent = true;
    }
}

function attachOnChangeToTemplates(contextPath, moduleType) {
    $('select#template').change(function () {
        var value = $('select#template option:selected').val();
        var insertFolderFlagValue = $('#insertFolderFlag').val();
        $.post(contextPath
            +
            "/plan/PlanCMOperations.action?showTemplate=", {
            planModuleType: moduleType,
            templateType: value,
            folderId: getSelectedTreeNodePath(true),
            insertFolderFlag: insertFolderFlagValue
        }, function (data) {
            //Update templateMainContent div
            $('#templateProperty').html(data);

        });
    });
}

function nl2br(str) {
    var breakTag = '<br />';
    return (str + '').replace(/([^>]?)\n/g, '$1 &nbsp;' + breakTag + '\n');
}

function removeFirstLine(text) {
    var index = text.indexOf('\n');
    return text.substring(index + 1);
}

function attachEventToPEIPreview(contextPath, peiId, path, moduleType) {
    $('#preview').click(function () {
        parent.window.location =
            contextPath + "/plan/Plan.action?planModuleType=" + moduleType + "&viewPEIPreview&peiId=" + peiId + "&path="
            + path;
    });
}

function attachEventToPEIView(contextPath, peiId, path, moduleType) {
    $('#view').click(function () {
        parent.window.location =
            contextPath + "/plan/Plan.action?planModuleType=" + moduleType + "&viewResource&peiId=" + peiId + "&path="
            + path;
    });
}

function addLinkRowToTemplate6DocumentElement(refreshIframe, href, alias) {
    var clone = $('#linksDivTemplate').clone();
    var html = clone.html();

    /* links alias id*/
    html = html.replace(/linkAliasId/g, "linkAliasId" + linksCounter);
    /* links alias name*/
    html = html.replace(/linkAliasName/g, "template.links[" + linksCounter + "].alias");
    /* remove row id*/
    html = html.replace(/removeLinkRow/g, "removeLinkRow" + linksCounter);
    /* link href id*/
    html = html.replace(/linkHrefId/g, "linkHrefId" + linksCounter);
    /* link href name*/
    html = html.replace(/linkHrefName/g, "template.links[" + linksCounter + "].href");

    $('#linksDiv').append("<div id=\"link" + linksCounter + "\" class=\"PEICMDocumentsProperty\">" + html
        + "</div>");
    $('#removeLinkRow' + linksCounter).click(function (e) {
        $('#link' + e.target.id.substring(13, e.target.id.length)).remove();
    });

    if (href != undefined) {
        $('#linkHrefId' + linksCounter).val(href);
    } else {
        $('#linkHrefId' + linksCounter).val('');
    }
    if (alias != undefined) {
        $('#linkAliasId' + linksCounter).val(alias);
    } else {
        $('#linkAliasId' + linksCounter).val('');
    }

    linksCounter++;
    if (refreshIframe) {
        iResizeInsideIFrame();
    }
}

function addFileRowToTemplate6DocumentElement(refreshIframe, contextPath, link, fileName, peiId, order, alias,
                                              moduleType) {
    var clone = $('#fileUploadTemplate').clone();
    var html = clone.html();

    /* file alias id */
    html = html.replace(/fileUploadAliasId/g, "fileUploadAlias" + fileCounter);
    /* file alias name*/
    html = html.replace(/fileUploadAliasName/g, "fileNames[" + fileCounter + "].alias");
    /* remove file row id*/
    html = html.replace(/removeFileRow/g, "removeFileRow" + fileCounter);
    /* file id*/
    html = html.replace(/fileUploadId/g, "fileT" + fileCounter);
    /* file name*/
    html = html.replace(/fileUploadName/g, "fileNames[" + fileCounter + "].file");

    if (contextPath != undefined) {
        html += "<a href=\"" + contextPath
            + "/plan/Plan.action?planModuleType=" + moduleType + "&viewResource=&peiViewOffline=true&path=" + link
            + "&peiId=" + peiId + "&order=" + order + "\" class=\"file-download\">" + fileName + "</a>";
    }
    html += '<input type=\"hidden\" name=\"filesChecker[' + fileCounter + ']\" value=\"true\"/>';

    $('#fileUploadDiv').append("<div id=\"fileUpload" + fileCounter + "\" class=\"PEICMDocumentsProperty\">" + html
        + "</div>");

    $('#removeFileRow' + fileCounter).click(function (e) {
        $('#fileUpload' + e.target.id.substring(13, e.target.id.length)).remove();
    });

    if (alias != undefined) {
        $('#fileUploadAlias' + fileCounter).val(alias);
    } else {
        $('#fileUploadAlias' + fileCounter).val('');
    }

    fileCounter++;
    if (refreshIframe) {
        iResizeInsideIFrame();
    }
}


function getContractNumberByPath(folderPath) {
    if (folderPath == null) {
        return;
    }
    var split = folderPath.split("/");
    if (split.length >= 3) {
        return split[2].substring(3, split[2].length);
    }
}

function addCMPermissionsRowEvent(rowId, contextPath, contractId, moduleType) {
    $('#' + rowId).click(function (e) {
        var ajaxLoad = false;

        if ($('#usedPermissionList' + rowId).html() == '') {
            ajaxLoad = true;
        } else {
            ajaxLoad = false;
        }
        if (ajaxLoad) {
            $.post(contextPath
                +
                "/plan/PlanCMPermissions.action?findPermissionUsages=", {
                planModuleType: moduleType,
                permissionId: rowId,
                contractId: contractId
            }, function (data) {
                $('#usedPermissionList' + rowId).html(data);
                setCMPermissionRowDisplay(rowId);
            });
        } else {
            setCMPermissionRowDisplay(rowId);
        }
    });

    $('#' + rowId).mouseover(function (e) {
        $('#' + rowId).css('background-color', '#39596E');
        $('#' + rowId).css('color', '#FFFFFF');
    });
    $('#' + rowId).mouseout(function (e) {
        $('#' + rowId).css('background-color', '#DFEAFF');
        $('#' + rowId).css('color', '#000000');
    });
}

function setCMPermissionRowDisplay(rowId) {
    var style = $('#row' + rowId).css('display');
    if (style == 'none' || style == '') {
        if (navigator.appName == "Microsoft Internet Explorer") {
            $('#row' + rowId).css('display', 'block');
        } else {
            $('#row' + rowId).css('display', 'table-row');
        }
    } else {
        $('#row' + rowId).css('display', 'none');
    }
}

function disableOrEnablePEIButtons(id, toEnable) {
    if (toEnable) {
        $("#" + id).removeAttr("disabled");
        $("#" + id).css('background-color', '');
    } else {
        $("#" + id).attr("disabled", "disabled");
        $("#" + id).css('background-color', '#999');
        $("#" + id).css('border', 'none');
    }
}

function attachEventToDropDownTemplateProcedure(contextPath, listId, listCode, moduleType) {
    $('#' + listId).change(function (e) {
        e.preventDefault();
        var params = '&procedureFilters[0]=' + $('#procedureFirstList').val();

        if (listCode == 2 || listCode == 3) {
            params += '&procedureFilters[1]=' + $('#procedureSecondList').val();
        }
        if (listCode == 3) {
            params += '&procedureFilters[2]=' + $('#procedureThirdList').val();
        }
        params += '&folder.path=' + $('#folderPath').val();
        params = encodeURI(params);

        $.get(contextPath + "/plan/PlanViewTemplate.action?planModuleType=" + moduleType
            + "&viewTemplate10ProcedureFragment=", params, function (data) {
            $('#template10procedureFragram').html(data);
        });
    });
}

function checkTemplateProcedureSelects() {
    if ($("#procedureFirstList option").length == 0) {
        $('#procedureFirstList').attr("disabled", "disabled");
    }
    if ($("#procedureSecondList option").length == 0) {
        $('#procedureSecondList').attr("disabled", "disabled");
    }
    if ($("#procedureThirdList option").length == 0) {
        $('#procedureThirdList').attr("disabled", "disabled");
    }
}

function attachEventToTemplate11MirrorLink(contextPath, fieldId, contractId, path, moduleType) {
    $("#" + fieldId).click(function () {
        parent.window.location =
            contextPath + "/plan/PlanCM.action?planModuleType=" + moduleType + "&viewPeiCMFromPreview=&contractId="
            + contractId + "&path="
            + encodeURI(path);
    });
}

function generateRandomString(length) {
    var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZ";
    var randomString = '';
    for (var i = 0; i < length; i++) {
        var rnum = Math.floor(Math.random() * chars.length);
        randomString += chars.substring(rnum, rnum + 1);
    }
    return randomString;
}

function attachViewTemplateDocxLink(confirmDeleteMessage) {
    $(".companySelectionLink").click(function (e) {
        e.preventDefault();
        var url = this.href;

        var xhr = $.get(url, null, function (data) {
            if (xhr.getResponseHeader('Stripes-Success') == "OK") {
                $('#companyId').html(data);

                // change links
                $("#aditionalLinks").empty();
                $(".copyCompany").clone().appendTo($("#aditionalLinks"));

                attachConfirmDelete(confirmDeleteMessage);

            } else {
                window.location.reload(true);
            }
        });

        // Make the user selected in the left column
        $(".companySelectionLink").each(function (i) {
            $(this).removeClass("selected");
        });

        $(this).addClass("selected");

        return false;
    });
}

function loadTemplateDocx(id, confirmDeleteMessage, planModuleType) {
    var user = $("#company" + id);
    var url;
    if (user.get(0) == null) {
        url = "/plan/PlanCMTemplatesDocx.action?viewTemplateDocxFragment=&template.id=" + id + "&planModuleType=" + planModuleType;
    }
    else {
        url = user.get(0).href;
    }
    var xhr = $.get(url, null, function (data) {
        if (xhr.getResponseHeader('Stripes-Success') == "OK") {
            $('#companyId').html(data);

            // change links
            $("#aditionalLinks").empty();
            $(".copyCompany").clone().appendTo($("#aditionalLinks"));

            attachConfirmDelete(confirmDeleteMessage);

        } else {
            window.location.reload(true);
        }
    });

    // Make the user selected in the left column
    $(".companySelectionLink").each(function (i) {
        $(user).removeClass("selected");
    });

    $(user).addClass("selected");
}

/**
 Security Function
 */


/**
 * Remove users from the list
 *
 * @param array     The list of users
 * @param property  The property to compare
 * @param value     The value to remove
 **/
function findAndRemove(array, property, value) {
    $.each(array, function (index, result) {
        if (result[property] == value) {
            array.splice(index, 1);
            return false;
        }
    });
}

function getChangeElement(id) {
    switch (id) {
        case "file-0" :
            return 0;
        case "file-1" :
            return 1;
        case "file-2" :
            return 2;
        case "file-3" :
            return 3;
        case "file-4" :
            return 4;
        case "file-5" :
            return 5;
        case "file-6" :
            return 6;
    }
}

function handleFileSelection(e) {
    var val = getChangeElement(e.currentTarget.id);
    var filename = null;

    if (typeof e.currentTarget.files === "undefined") { // ie
        filename = $("#file-" + val).val();
        filename = filename.substring(filename.lastIndexOf("\\") + 1, filename.length);
    } else if (typeof e.currentTarget.files[0] !== "undefined") { // decent browser
        filename = e.currentTarget.files[0].name;
    }

    if (filename === null) { // the user cancelled
        $('#file-name-' + val).val("");
    } else { // a file was selected
        $('#file-name-' + val).val(filename);
        if (val === lastEnabledAttachmentInput && attachments < ATTACHMENT_MAX_SIZE) {
            attachments++;
            lastEnabledAttachmentInput = attachments;
            $('#file-' + attachments).addClass("attachmentsPosition");
            $('#file-' + attachments).show();
            $('#file-name-' + attachments).addClass("attachmentsPosition");
            $('#file-name-' + attachments).show();
            $('#file-label-' + attachments).addClass("attachmentsPosition");
            $('#file-label-' + attachments).show();
        }
    }
}
