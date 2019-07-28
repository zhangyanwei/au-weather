/**
 * If you want to know how to use the STOMP client, please check the link: http://jmesnil.net/stomp-websocket/doc/
 */
(function() {

    let stompClient;

    /**
     * Open the web socket connection and subscribe the "/notify" channel.
     */
    function connect(currentCityCode) {
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        // In this case, for each connection, only allowed subscribe to one city's weather,
        // that why supply the city code when establishing the connection.
        stompClient.connect({ cityCode: currentCityCode }, function() {
            stompClient.subscribe('/user/queue/weather', function(message) {
                refreshWeather(JSON.parse(message.body));
            });
            stompClient.send("/app/weather", {}, currentCityCode)
        });
    }

    /**
     * Close the web socket connection
     */
    function disconnect() {
        stompClient && stompClient.disconnect()
    }

    /**
     * Refresh the selected city weather.
     */
    function refreshWeather(weather) {
        $('#w-city').text(weather.city);
        $('#w-weather').text(weather.weather);
        $('#w-temperature').text(weather.temperature);
        $('#w-wind').text(weather.wind);
        $('#w-time').text(weather.updatedTime)
    }

    $(document).ready(function () {
        $('.dropdown').on('click', '.dropdown-item', function () {
            const $this = $(this);
            const $dropdown = $this.closest('.dropdown');
            const previousCityCode = $dropdown.data('value');
            const currentCityCode = $this.data('value');
            if (previousCityCode !== currentCityCode) {
                $dropdown
                    .data('value', currentCityCode)
                    .find('.dropdown-toggle').text($this.text());
                disconnect();
                connect(currentCityCode);
            }
        })
    });
})();
