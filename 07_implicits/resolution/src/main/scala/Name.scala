case class Name(value: String)

object Albania {
  implicit val defaultAlbanian = Name("Boban Jones")
}

object Australia {
  implicit val defaultAustralian = Name("Bruce")
}
