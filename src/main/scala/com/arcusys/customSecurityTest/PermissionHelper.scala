package com.arcusys.customSecurityTest

import javax.portlet.PortletRequest
import com.liferay.portal.kernel.util.WebKeys
import com.liferay.portal.theme.ThemeDisplay
import com.liferay.portal.util.PortalUtil
import com.liferay.portal.kernel.portlet.LiferayPortletSession
import com.liferay.portal.service._
import com.liferay.portal.model.ResourceConstants

object PermissionHelper {

  def hasPermission(request: PortletRequest, action: String): Boolean = {
    val themeDisplay = request.getAttribute(WebKeys.THEME_DISPLAY).asInstanceOf[ThemeDisplay]

    val portletId = PortalUtil.getPortletId(request)
    val groupId = themeDisplay.getScopeGroupId
    val primaryKey = themeDisplay.getLayout.getPlid + LiferayPortletSession.LAYOUT_SEPARATOR + portletId

    themeDisplay.getPermissionChecker.hasPermission(groupId, portletId, primaryKey, action)
  }

  def hasPermission(request: PortletRequest, action: String, resourceName: String, primaryId: Long): Boolean = {
    val themeDisplay = request.getAttribute(WebKeys.THEME_DISPLAY).asInstanceOf[ThemeDisplay]

    val groupId = themeDisplay.getScopeGroupId

    themeDisplay.getPermissionChecker.hasPermission(groupId, resourceName, primaryId, action)
  }

  def addResource(request: PortletRequest, resourceName: String, resourceId: Long) {
    val themeDisplay = request.getAttribute(WebKeys.THEME_DISPLAY).asInstanceOf[ThemeDisplay]

    val companyId = themeDisplay.getCompanyId
    val groupId = themeDisplay.getScopeGroupId
    val userId = themeDisplay.getUserId

    ResourceLocalServiceUtil.addResources(companyId, groupId, userId, resourceName, resourceId, false, false, true)
  }

  def removeResource(request: PortletRequest, resourceName: String, resourceId: Long) = {
    ResourceLocalServiceUtil.deleteResource(PortalUtil.getCompanyId(request), resourceName, ResourceConstants.SCOPE_INDIVIDUAL, resourceId)
  }
}
