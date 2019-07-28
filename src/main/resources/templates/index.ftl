<!doctype html>
<!-- For the top-level HTML elements, not need to add indent on them.  -->
<html>
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Description -->
    <meta name="description" content="It's a simple web site for an AU job.">
    <meta name="author" content="zhangyanwei">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">

    <title>Weather Report</title>
</head>
<body>

<!-- Widget [weather] -->
<div class="container widget">
    <div class="row mt-3">
        <div class="col">
            <!-- Dropdown [city] -->
            <div class="dropdown">
                <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdown-cities" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    Please select
                </button>
                <div class="dropdown-menu" aria-labelledby="dropdown-cities">
                    <#list cities as city>
                    <a class="dropdown-item" href="#" data-value="${city.code}">${city.name}</a>
                    </#list>
                </div>
            </div>
        </div>
    </div>
    <div class="row mt-3">
        <div class="col-md-3 m-auto">
            <!-- Table [weather] -->
            <table class="table">
                <tbody>
                <tr>
                    <th scope="row">City</th>
                    <td id="w-city"></td>
                </tr>
                <tr>
                    <th scope="row">Updated time</th>
                    <td id="w-time"></td>
                </tr>
                <tr>
                    <th scope="row">Weather</th>
                    <td id="w-weather"></td>
                </tr>
                <tr>
                    <th scope="row">Temperature</th>
                    <td id="w-temperature"></td>
                </tr>
                <tr>
                    <th scope="row">Wind</th>
                    <td id="w-wind"></td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>


<!-- Optional JavaScript -->
<!-- jQuery first, then Popper.js, then Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

<!-- STOMP -->
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="/lib/stomp.min.js"></script>

<!-- WEB -->
<script src="/js/actions.js"></script>
</body>
</html>
