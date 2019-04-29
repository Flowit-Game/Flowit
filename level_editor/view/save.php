<div class="container p-3">
    <?php if (@$_SESSION["solved"]) { ?>
        <div class="card p-0 m-2 mx-auto">
            <div class="card-header">
                Save code
            </div>
            <div class="card-body">
                <textarea spellcheck="false" class="form-control w-50 mx-auto" rows="20"><?php
                    include("logic/levelCode.php");
                    echo getLevelCode();
                ?></textarea>
            </div>
        </div>
    <?php } else { ?>
        <div class="alert alert-danger">
            Please solve the design by playing before saving.
        </div>
    <?php } ?>
</div>