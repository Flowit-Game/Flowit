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
                        Your name to be listed in the credits section
                        <input type="text" class="form-control" placeholder="Name" name="name" />
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" name="checkAgree" id="checkAgree">
                        <label class="form-check-label" for="checkAgree">
                            I agree that the level I designed will be included in the app Flowit. I provide a license for this use free of charge.
                        </label>
                    </div>
                    <input type="hidden" name="save" value="true">
                    <input class="btn btn-primary mt-2" type="submit" value="Send">
                </form>
            </div>
        </div>
    <?php } else { ?>
        <div class="alert alert-danger">
            The current design was not solved yet. To save, you need to solve the level by playing.
        </div>
    <?php } ?>
</div>