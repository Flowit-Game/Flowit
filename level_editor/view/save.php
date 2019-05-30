<div class="container p-3">
    <?php
        if (@$sendResult != "") {
            echo $sendResult;
        }
    ?>
    <?php if (@$_SESSION["solved"]) { ?>
        <div class="card p-0 m-2 mx-auto">
            <div class="card-header">
                Save level
            </div>
            <div class="card-body">
                <form action="?action=source" method="post">
                    <div class="form-group">
                        Your name to be listed in the credits section. Required.
                        <input type="text" class="form-control" placeholder="Name" name="name" />
                    </div>
                    <div class="form-group">
                        Your email address if you want to be notified as soon as the level was included.
                        <input type="email" class="form-control" placeholder="Email" name="email" />
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" name="checkAgree" id="checkAgree">
                        <label class="form-check-label" for="checkAgree">
                            I agree that the level I designed will be included in the app Flowit. I provide a license for this use free of charge. Required.
                        </label>
                    </div>
                    <input type="hidden" name="save" value="true">
                    <input class="btn btn-primary mt-2" type="submit" value="Send">
                </form>
            </div>
        </div>
        <?php if ($_SERVER["SERVER_ADDR"] === $_SERVER["REMOTE_ADDR"]) { ?>
            <div class="card p-0 m-2 mx-auto">
                <div class="card-header">
                    Level code
                </div>
                <div class="card-body">
                    <pre><?php
                            $code = getLevelCode("");
                            $code = str_replace("<", "&lt;", $code);
                            $code = str_replace(">", "&gt;", $code);
                            echo $code;
                    ?></pre>
                </div>
            </div>
        <?php } ?>
    <?php } else { ?>
        <div class="alert alert-secondary">
            The current design was not solved yet. The level can only be submitted if it can be solved.
            <a class="btn btn-primary" href="./?action=play&play=restart">Play level now</a>
        </div>
    <?php } ?>
</div>