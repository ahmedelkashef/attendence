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
             String isLogged = (String)request.getSession().getAttribute("isLoggedIn");
             String error = (String)request.getSession().getAttribute("error");
             if (error == null){
            	 error = " ";
             }
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
                <a href="#" class="dropdown-toggle" >
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

      <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
          <h1>
            500 Error Page
          </h1>
          <ol class="breadcrumb">
            <li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
            <li><a href="#">Examples</a></li>
            <li class="active">500 error</li>
          </ol>
        </section>

        <!-- Main content -->
        <section class="content">

          <div class="error-page">
            <h2 class="headline text-red">500</h2>
            <div class="error-content">
              <h3><i class="fa fa-warning text-red"></i> Oops! Something went wrong.</h3>
              <h4><strong><%=error %></strong></h4>
              <p>
                We will work on fixing that right away.
                Meanwhile, you may <a href="html/index.html">return to dashboard</a> or try using the search form.
              </p>
              <!-- <form class="search-form">
                <div class="input-group">
                  <input type="text" name="search" class="form-control" placeholder="Search">
                  <div class="input-group-btn">
                    <button type="submit" name="submit" class="btn btn-danger btn-flat"><i class="fa fa-search"></i></button>
                  </div>
                </div>/.input-group
              </form> -->
            </div>
          </div><!-- /.error-page -->

        </section><!-- /.content -->
      </div><!-- /.content-wrapper -->
<%} %>
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

  </body>
</html>
