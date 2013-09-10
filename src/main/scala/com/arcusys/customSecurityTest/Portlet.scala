package com.arcusys.customSecurityTest

import javax.portlet._
import org.scalatra.ScalatraFilter
import java.util.Date
import com.arcusys.scala.scalatra.mustache.MustacheSupport
import com.arcusys.customSecurityTest.repository.MessagesRepository
import com.liferay.portal.security.auth.PrincipalException

class Portlet extends GenericPortlet with ScalatraFilter with MustacheSupport {

  val VIEW_MESSAGE_ACTION = "VIEW_MESSAGE_ACTION"
  val ADD_MESSAGE_ACTION = "ADD_MESSAGE_ACTION"
  val REMOVE_MESSAGE_ACTION = "REMOVE_MESSAGE_ACTION"

  val RESOURCE_NAME = "com.arcusys.customSecurityTest.model.Message"

  override def destroy() {}

  override def doView(request: RenderRequest, response: RenderResponse) {
    val data = Map(
      "resourceUrl" -> response.createResourceURL,

      "custom-administrator-rights" -> request.isUserInRole("custom-administrator"),
      "user-rights" -> request.isUserInRole("user"),

      "VIEW-available" -> PermissionHelper.hasPermission(request, "VIEW"),
      "CONFIGURATION-available" -> PermissionHelper.hasPermission(request, "CONFIGURATION"),
      "ADD_TO_PAGE-available" -> PermissionHelper.hasPermission(request, "ADD_TO_PAGE"),

      "messages" -> MessagesRepository.getAll.filter(message => PermissionHelper.hasPermission(request, VIEW_MESSAGE_ACTION, RESOURCE_NAME, message.id)),
      "ADD_MESSAGE_ACTION-available" -> PermissionHelper.hasPermission(request, ADD_MESSAGE_ACTION)
    )

    response.getWriter.println(mustache(data, "index.html"))
  }

  override def serveResource(request: ResourceRequest, response: ResourceResponse) {
    request.getParameter("action") match {
      case "add" => add(request, response)
      case "remove" => remove(request, response)
      case _ => {}
    }
    super.serveResource(request, response)
  }

  def add(request: ResourceRequest, response: ResourceResponse) {
    if (!PermissionHelper.hasPermission(request, ADD_MESSAGE_ACTION)) throw new PrincipalException()

    val message = MessagesRepository.create(request.getParameter("text"))

    PermissionHelper.addResource(request, RESOURCE_NAME, message.id)
  }

  def remove(request: ResourceRequest, response: ResourceResponse) {
    val messageId = request.getParameter("id").toInt

    if (!PermissionHelper.hasPermission(request, REMOVE_MESSAGE_ACTION, RESOURCE_NAME, messageId)) throw new PrincipalException()

    MessagesRepository.remove(messageId)
    PermissionHelper.removeResource(request, RESOURCE_NAME, messageId)
  }
}

