<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>All Your New Recipes</title>
</head>
<link
	href="https://stackpath.bootstrapcdn.com/bootswatch/4.1.3/minty/bootstrap.min.css"
	rel="stylesheet">
<body>
<form action="/all-new-recipes" method= "post">
	<%@ include file="navbar.jsp"%>
	<%-- <h1>HERE ARE ALL OF YOUR RECIPES</h1>
	<th>Name</th>
	<th>Time</th>
	<th>Yeild</th>
	<th>Ingredients</th>
	<table class="table-hover"> 
	<c:forEach var="meals" items="${myMeals}">
		<p>${meals}</p>
	</c:forEach>
	</table> --%>
	
	<table class="table">
            <thead>
                <tr>
                  <th>Name</th><th>Time</th> <th>Yeild</th><th>Ingredients</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="meals" items="${myMeals}">
                
                <tr>
                    <td>${meals.label}</td> <td> ${meals.totalTime}</td>
                   <%--  <td>
						<a href="/update-item?id=${ items.id }" class="btn btn-light btn-sm">Edit</a>
						<a href="/delete?id=${meals.id }" class="btn btn-light btn-sm" onclick= "return confirm('are you sure?')">Delete</a>
						
					</td>  --%>
                </tr>
               
                </c:forEach>
            </tbody>
        </table>	
</body>
</html>