<%@ page import= "codeu.model.store.basic.UserStore" %>

<nav>
    <a id="navTitle" href="/">
    <img id="mapIt" src="/About-IMG/Map-it(Black).png"> </a>  
    <% String username = (String) request.getSession().getAttribute("user");
        if(username != null){ %>
      <a  href= "/profile/<%=username%>">Profile</a>
      <a href="/logout">Logout</a>
      <a href="/conversations">Conversations</a>
     <% } else{ %>
      <a href="/login">Login</a>
    <% } %>
    <a href="/map">Map</a>
    <a href="/about.jsp">About</a>
    <a href="/activity">Activity</a>
    <% 
    if(username !=null && UserStore.getInstance().hasAdmin(username)){
    %>
     <a href="/admin">Admin</a> 
     <% } %>
</nav>