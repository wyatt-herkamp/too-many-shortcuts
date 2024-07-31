
[positional-arguments]
@publishMaven *args='':
    echo "Publishing to Maven with User" $1 "and Password" $2
    ./gradlew publish -Pkingtux_dev_username="$1" -Pkingtux_dev_password="$2"
