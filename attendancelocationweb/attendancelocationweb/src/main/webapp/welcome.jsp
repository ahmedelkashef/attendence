<%@page import="com.google.api.services.admin.directory.model.Domains"%>
<%@page import="com.google.api.services.admin.directory.model.Domains2"%>
<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html;charset=UTF-8"%>
<%@page import="com.cloudypedia.servlet.Directory_Servlet"%>
<%@page import="com.google.api.services.admin.directory.model.Users"%>

<%@page import="com.google.api.services.admin.directory.model.User"%>
<%@page import="com.google.api.services.admin.directory.Directory"%>

<%@ page import=" com.google.api.client.googleapis.auth.oauth2.GoogleCredential"%>
<%@ page import="com.google.api.client.http.HttpTransport"%>
<%@ page import="com.google.api.client.http.javanet.NetHttpTransport"%>
<%@ page import="com.google.api.client.json.JsonFactory"%>
<%@ page import="com.google.api.client.json.jackson2.JacksonFactory"%>
<%@ page
	import="com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl"%>
<%@ page
	import=" com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest"%>
<%@ page
	import=" com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse"%>
<%@ page
	import=" com.google.api.client.googleapis.auth.oauth2.GoogleCredential"%>

<%@ page import="java.util.List"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.File"%>

<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<html>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>ABC Starter</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap 3.3.5 -->
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">
    <!-- Ionicons -->
    <link rel="stylesheet" href="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="dist/css/AdminLTE.min.css">
    <link rel="stylesheet" href="dist/css/skins/skin-blue.min.css">
    <link rel="stylesheet" href="plugins/iCheck/all.css">

  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
  <meta name="google-signin-scope" content="profile email">
	    <meta name="google-signin-client_id" content="440275256424-vvta4q6nf3bj7h4bb3o3h5gkcf3h3hu1.apps.googleusercontent.com">
	    <script src="https://apis.google.com/js/platform.js" async defer></script>
    <script src="https://apis.google.com/js/client:platform.js" type="text/javascript"></script>
<style>
 
  </style>
  
  </head>
 
    
  <body class="hold-transition skin-blue sidebar-mini" >
   
    <div class="wrapper">

      <!-- Main Header -->
     <%
     Map map = request.getParameterMap();
     for(Object k : map.keySet()){
    	 %>
    	 <%="param: " + k.toString() + " = " + map.get(k)%>
    	 <%
     }
     %>
             <%
             String Code = "";
             String IsCode = (String) request.getSession(false).getAttribute("code");
             if (IsCode != null){
            	 Code = IsCode;
             }
             else{
            	 Code = request.getParameter("code");
            	 request.getSession(false).setAttribute("code", request.getParameter("code"));
             }
             String isLogged = (String)request.getSession().getAttribute("isLoggedIn");
             String Domain = null;
             boolean isLoggedIn = false;
             if (isLogged != null){
            	 if (isLogged.equals("true"))
                  	isLoggedIn = true;
             }
             else{
            	 isLogged = "false";
            	 isLoggedIn = false;
            	 
             }
            	 
             

             Domain = request.getSession().getAttribute("Domain").toString();
             if (request.getParameter("code") != null)
             {
            	 request.getSession(false).setAttribute("code", request.getParameter("code"));
            	 Code = request.getParameter("code");
             }
             else
             {
            	 Code = "";
             }
             
               		String email = "";
					String domain = "";
					String Username = "";
					UserService userService = UserServiceFactory.getUserService();
					
					//com.google.appengine.api.users.User user = userService.getCurrentUser();
					
				
					if (!isLoggedIn) {
						
					%>
					 <header class="main-header">

        <!-- Logo -->
        <a href="index2.html" class="logo">
          <!-- mini logo for sidebar mini 50x50 pixels -->
          <span class="logo-mini"><b>ABC</b></span>
          <!-- logo for regular state and mobile devices -->
          <span class="logo-lg"><b>ABC Application</b></span>
        </a>

        <!-- Header Navbar -->
        <nav class="navbar navbar-static-top" role="navigation">
          <!-- Sidebar toggle button-->
          <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
            <span class="sr-only">Toggle navigation</span>
          </a>
          <!-- Navbar Right Menu -->
          <div class="navbar-custom-menu">
            <ul class="nav navbar-nav">
              <!-- Messages: style can be found in dropdown.less-->
              

              <!-- Notifications Menu -->
					<!-- User Account Menu -->
              <li class="dropdown user user-menu">
                <!-- Menu Toggle Button -->
                <a href="<%=userService.createLoginURL(request.getRequestURI())%>" class="dropdown-toggle" >
                <span class="hidden-xs" > Sign In </span>
               
                </a>
                     
                  </li>
                 
               
              </li>
              <!-- Control Sidebar Toggle Button -->
              <li>
                <a href="#" data-toggle="control-sidebar"><i class="fa fa-gears"></i></a>
              </li>
            </ul>
          </div>
        </nav>
      </header>

      <!-- Content Wrapper. Contains page content -->
      <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
          <h1>
            Please Sign in with your google Apps Super Admin's Account.
            
          </h1>
          <ol class="breadcrumb">
            <li><a href="#"><i class="fa fa-dashboard"></i> Level</a></li>
            <li class="active">Here</li>
          </ol>
        </section>
					<% }
					else{
						
						System.out.println("hereeeee2222");
						 /* email = userService.getCurrentUser().getEmail(); */

							/*System.out.println("hereeeee" + email);  */
						/* int index = email.indexOf('@');
						Username = email.substring(0,index);
						domain = email.substring(email.indexOf("@") + 1);  */
						//String DD = request.getSession(false).getAttribute("ClDomain");
						if (Domain == null) {
							System.out.println("DOMAIN NULL");
							request.getSession().setAttribute("error","please enter a valid domain");
							response.sendRedirect("/error.jsp");
							return;
						}
						/* if (DD != domain )
						{
							System.out.println("DOMAIN Diff");
							request.getSession().setAttribute("error","please Sign in with the same domain you wrote");
							response.sendRedirect("/error.jsp");
							return;
						} */
						String currURL = "";
						if (request.getServerName().equalsIgnoreCase("localhost"))
							currURL = request.getScheme() + "://"
									+ request.getServerName() + ":8888"
									+ request.getRequestURI();

						else
							currURL = request.getScheme() + "://"
									+ request.getServerName() + request.getRequestURI();
								
							
									Directory dir = Directory_Servlet.authenticate(request.getParameter("code"), currURL);
										System.out.println("Directory done");
										
										try {
											/* System.out.println("email name: " + email );*/
											dir.users().list().setCustomer("my_customer").setQuery("isAdmin=true").execute();
											System.out.println("is super admin");
										} 
										catch (Exception e) {
											e.printStackTrace();
											System.out.println("not super admin");
											request.getSession().setAttribute("error",
													"Sorry you are not a super admin");
											response.sendRedirect("/error.jsp");
											return;
										}
						
					%>
					 <header class="main-header">
					 <!-- Logo -->
        <a href="index2.html" class="logo">
          <!-- mini logo for sidebar mini 50x50 pixels -->
          <span class="logo-mini"><b>ABC</b></span>
          <!-- logo for regular state and mobile devices -->
          <span class="logo-lg"><b>ABC Application</b></span>
        </a>
					  <nav class="navbar navbar-static-top" role="navigation">
				          <!-- Sidebar toggle button-->
				          <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
				            <span class="sr-only">Toggle navigation</span>
				          </a>
				          <!-- Navbar Right Menu -->
				          <div class="navbar-custom-menu">
				            <ul class="nav navbar-nav">
				              <!-- User Account Menu -->
				              <li class="dropdown user user-menu">
                <!-- Menu Toggle Button -->
                 <a href="#" class="dropdown-toggle" ><span class="hidden-xs"> Signed In </span> 
             
                  <!-- The user image in the navbar-->
                  <img src="dist/img/user2-160x160.jpg" class="user-image" alt="User Image">
                  <!-- hidden-xs hides the username on small devices so only the image appears. -->
                  
                </a>
                
              </li>
              <!-- Control Sidebar Toggle Button -->
              <li>
                <a href="#" data-toggle="control-sidebar"><i class="fa fa-gears"></i></a>
              </li>
            </ul>
          </div>
        </nav>
      </header>

      <!-- Content Wrapper. Contains page content -->
      <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
          <br>
         
          <div class="container">
          <table id="userstable" class="table table-bordered table-hover">
           <thead>
                      <tr>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Subscribe</th>
                        
                      </tr>
           </thead>
          <tbody>
             <% 
            String PageToken = "";
            Users result = dir.users().list().setCustomer("my_customer").setMaxResults(500).setOrderBy("email").execute();
            List<User> Users = result.getUsers();
            
            Domains2 domainResult = dir.domains().list("my_customer").execute();
            List<Domains> domainss = domainResult.getDomains();
            
            HashSet<String> admins = new HashSet<String>();
            HashSet<String> domains = new HashSet<String>();
            
            for(User user : Users){
            	if(user.getIsAdmin()){
            		admins.add(user.getPrimaryEmail().toLowerCase());
            	}
            }
            session.setAttribute("usersList", admins);
            
            for(Domains d : domainss){
            	if(d.getVerified()){
            		domains.add(d.getDomainName().toLowerCase());
            	}
            }
            session.setAttribute("domains", domains);
            
            if (Users == null || Users.size() == 0) 
            {
                System.out.println("No users found.");
            } 
            else 
            {
                System.out.println("Users:");
                PageToken = result.getNextPageToken();
                for (User US : Users) {
                %>
                <tr>
                <td>
                 <%= US.getName().getFullName() %>
                 <input type="hidden" name="<%=US.getId()%>" value="<%= US.getPrimaryEmail()%>" > 
                </td>
                <td>
                	<%= US.getPrimaryEmail().toString()%></td>
                	<td>
                	<input type="checkbox" class="flat-red" name="user" value="<%=US.getId()%>">
                	</td>
                	</tr>
                	
                	<%
                }
                
            }
            
            %>
            </tbody> 
              </table> 
              </div>
             <br>
             <form id="userForm" method="post">
             <input type="hidden" id="domain" name="domain" value="<%=Domain%>" >  
             <input type="hidden" id="code" name="code" value="<%=IsCode%>">
              <input type="hidden" id="isLogged" name="isLogged" value="<%=isLogged%>">
              <input type="hidden" id="list" name="list">
              <input type="hidden" id="Nlist" name="Nlist">
              <input type="hidden" id="Users" name="Users" value="<%=Users%>">
               <input type="hidden" id="currURL" name="currURL" value="<%=currURL%>">
               <input type="hidden" id="adminEmail" name="adminEmail" value="<%=" "%>">
             <button type="button" class="btn btn-primary" data-toggle="button" aria-pressed="false" autocomplete="off" id="Sbutton">
			  Submit
			</button>
			</form>
			
						
         
          <ol class="breadcrumb">
            <li><a href="#"><i class="fa fa-dashboard"></i> Level</a></li>
            <li class="active">Here</li>
          </ol>
        </section>
        <%} %>
<!-- /////////////////////////////////////////////////////////////////////////////////// -->
        <!-- Main content -->
        <section class="content">

          <!-- Your Page Content Here -->

        </section><!-- /.content -->
      </div><!-- /.content-wrapper -->

      <!-- Main Footer -->
      <footer class="main-footer">
        <!-- To the right -->
        <div class="pull-right hidden-xs">
          Anything you want
        </div>
        <!-- Default to the left -->
        <strong>Copyright &copy; 2015 <a href="#">Cloudypedia</a>.</strong> All rights reserved.
      </footer>

    </div><!-- ./wrapper -->

    <!-- REQUIRED JS SCRIPTS -->

    <!-- jQuery 2.1.4 -->
    <script src="plugins/jQuery/jQuery-2.1.4.min.js"></script>
    <!-- Bootstrap 3.3.5 -->
    <script src="bootstrap/js/bootstrap.min.js"></script>
    <!-- AdminLTE App -->
    <script src="dist/js/app.min.js"></script>
     <script src="plugins/jQuery/jQuery-2.1.4.min.js"></script>
    <!-- Bootstrap 3.3.5 -->
    <script src="bootstrap/js/bootstrap.min.js"></script>
    <!-- DataTables -->
    <script src="plugins/datatables/jquery.dataTables.min.js"></script>
    <script src="plugins/datatables/dataTables.bootstrap.min.js"></script>
    <!-- SlimScroll -->
    <script src="plugins/slimScroll/jquery.slimscroll.min.js"></script>
    <!-- FastClick -->
    <script src="plugins/fastclick/fastclick.min.js"></script>
    <!-- AdminLTE App -->
    <script src="dist/js/app.min.js"></script>
    <!-- AdminLTE for demo purposes -->
    <script src="dist/js/demo.js"></script>
<script type="text/javascript">
var userArray = [];
var NameArray = [];
    $(document).ready(function(){
        $('input[type="checkbox"]').click(function(){
            if($(this).prop("checked") == true){
            	var Email = $(this).val();
            	userArray.push($(this).val());
            	
            	NameArray.push($('input[name="'+Email+'"]').val());
            	//alert("Checkbox is checked." + NameArray);
            }
            else if($(this).prop("checked") == false){
            	var index = userArray.indexOf($(this).val());
            	userArray.splice(index, 1);
            	
            	var NameIndex  = NameArray.indexOf($('input[name="'+$(this).val()+'"]').val());
            	NameArray.splice(NameIndex,1);
                /* alert("Checkbox is unchecked: " + userArray); */
            }
            document.getElementById("list").value = userArray;
            document.getElementById("Nlist").value = NameArray;
            
        });
        
    	$("#Sbutton").click(function(){
			  $.ajax({
				   type: "POST",
		           url: "../Util",
		           data: {"domain":$("#domain").val(),"code":$("#code").val(),"currURL":$("#currURL").val(),"list":$("#list").val(),"Nlist":$("#Nlist").val(),"adminEmail":$("#adminEmail").val(),"func":"addtoDatastore"},
				     success: function (data) {
				    	 alert("done");
				    	 
				     }
				});    
		});
        
    });
</script>
<!-- page script -->
    <script>
      $(function () {
        
        $('#userstable').DataTable({
          "paging": true,
          "lengthChange": false,
          "searching": false,
          "ordering": true,
          "info": true,
          "autoWidth": false
        });
      });
    </script>
  </body>
</html>
