<div id="imageMap">
    ${requestScope.actionBean.folder.template.imageMap}
</div>

<script type="text/javascript">

    $(document).ready(function()
    {
        try {
            $('.peiContent img').removeAttr('alt');

            // Use the each() method to gain access to each elements attributes
            $('area').each(function()
            {
                var title = $(this).attr('title')
                $(this).attr('title', nl2br(removeFirstLine(title)));

                $(this).qtip(
                {
                    content: $(this).attr('title'),
                    hide: { when: { event: 'unfocus' } },
                    show: { when: { event: 'click' } },
                    position: {

                        adjust: { screen: true }
                    },

                    style: {
                        name: 'dark', // Give it the preset dark style
                        border: {
                            width: 0,
                            radius: 4
                        },
                        padding: '7px 13px',
                        width: {
                            max: 400
                        },
                        tip: true // Apply a tip at the default tooltip corner
                    }
                }).attr('title', '').removeAttr('href').removeAttr('alt');
            });


            $(function() {
                $('#imageMap').find("img").maphilight();
            });

        } catch(err) {
        }
    });

</script>