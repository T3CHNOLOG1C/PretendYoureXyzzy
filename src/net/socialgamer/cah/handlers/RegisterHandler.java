package net.socialgamer.cah.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import net.socialgamer.cah.Constants.AjaxOperation;
import net.socialgamer.cah.Constants.AjaxResponse;
import net.socialgamer.cah.Constants.ErrorCode;
import net.socialgamer.cah.Constants.ReturnableData;
import net.socialgamer.cah.Server;
import net.socialgamer.cah.data.ConnectedUsers;
import net.socialgamer.cah.data.User;

import com.google.inject.Inject;


public class RegisterHandler extends Handler {

  public static final String OP = AjaxOperation.REGISTER.toString();

  private static final Pattern validName = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]{2,29}");

  private final ConnectedUsers users;

  /**
   * I don't want to have to inject the entire server here, but I couldn't figure out how to
   * properly bind ConnectedUsers in Guice, so here we are for now.
   * 
   * @param server
   */
  @Inject
  public RegisterHandler(final Server server) {
    this.users = server.getConnectedUsers();
  }

  @Override
  public Map<ReturnableData, Object> handle(final Map<String, String[]> parameters,
      final HttpSession session) {
    final Map<ReturnableData, Object> data = new HashMap<ReturnableData, Object>();

    if (!parameters.containsKey("nickname") || parameters.get("nickname").length != 1) {
      return error(ErrorCode.NO_NICK_SPECIFIED);
    } else {
      final String nick = parameters.get("nickname")[0].trim();
      if (!validName.matcher(nick).matches()) {
        return error(ErrorCode.INVALID_NICK);
      } else if (users.hasUser(nick)) {
        return error(ErrorCode.NICK_IN_USE);
      } else {
        final User user = new User(nick);
        users.newUser(user);
        session.setAttribute("user", user);

        data.put(AjaxResponse.NICKNAME, nick);
      }
    }
    return data;
  }
}
